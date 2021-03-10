
package io.vera.server.world;

import io.vera.meta.nbt.Compound;
import io.vera.server.entity.VeraEntity;
import io.vera.world.opt.Dimension;
import io.vera.world.opt.WorldCreateSpec;
import lombok.Getter;
import io.vera.world.other.Position;
import io.vera.entity.Entity;
import io.vera.entity.living.Player;
import io.vera.meta.nbt.Tag;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.packet.play.PlayOutTime;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.opt.GenOptImpl;
import io.vera.server.world.opt.WeatherImpl;
import io.vera.server.world.opt.WorldBorderImpl;
import io.vera.server.world.opt.WorldOptImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@ThreadSafe
public class World {

    private static final ServerThreadPool TP = ServerThreadPool.forSpec(PoolSpec.WORLDS);

    private final Runnable tickingTask = this::doTick;

    @Getter
    private final ChunkMap chunks = new ChunkMap(this);
    @Getter
    private final String name;
    @Getter
    private final Path directory;
    @Getter
    private final Dimension dimension;
    @Getter
    private final WorldOptImpl worldOptions;
    @Getter
    private final GenOptImpl generatorOptions;
    @Getter
    private final WorldBorderImpl border = new WorldBorderImpl(this);
    @Getter
    private final WeatherImpl weather = new WeatherImpl(this);

    private final AtomicInteger time = new AtomicInteger();
    @Getter
    private final LongAdder age = new LongAdder();
    @Getter
    private final Set<VeraPlayer> occupants = Collections.newSetFromMap(new ConcurrentHashMap<>());
    @Getter
    private final Set<VeraEntity> entitySet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public World(String name, Path enclosing, WorldCreateSpec spec) {
        this.name = name;
        this.directory = enclosing;
        this.dimension = spec.getDimension();

        this.generatorOptions = new GenOptImpl(spec);
        this.worldOptions = new WorldOptImpl(this, spec);
    }

    public World(String name, Path enclosing, Dimension dimension) {
        this.name = name;
        this.directory = enclosing;
        this.dimension = dimension;

        try (GZIPInputStream stream = new GZIPInputStream(new FileInputStream(this.directory.resolve("level.dat").toFile()))) {
            Compound root = Tag.decode(new DataInputStream(stream));
            Compound compound = root.getCompound("Data");

            this.generatorOptions = new GenOptImpl(compound);
            this.worldOptions = new WorldOptImpl(this, compound);
            this.weather.read(compound);
            this.border.read(compound);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSpawnChunks() {
        int centerX = this.worldOptions.getSpawn().getIntX() >> 4;
        int centerZ = this.worldOptions.getSpawn().getIntZ() >> 4;
        int radius = 3;
        for (int x = centerX - radius; x < centerX + radius; x++) {
            for (int z = centerZ - radius; z < centerZ + radius; z++) {
                this.getChunkAt(x, z);
            }
        }
    }

    public final void tick() {
        TP.execute(this.tickingTask);
    }

    private void doTick() {
        this.age.increment();

        int curTime;
        int newTime;
        do {
            curTime = this.time.get();
            newTime = curTime + 1;
            if (newTime == 24000) {
                newTime = 0;
            }
        } while (!this.time.compareAndSet(curTime, newTime));

        if (newTime == 0) {
            RecipientSelector.inWorld(this, new PlayOutTime(this.age.longValue(), newTime));
        }

        this.weather.tick();
        this.border.tick();

        this.chunks.forEach(Chunk::tick);
    }

    
    public int getTime() {
        return this.time.get();
    }

    
    public Set<? extends Player> getPlayers() {
        return Collections.unmodifiableSet(this.occupants);
    }

    
    public Stream<? extends Entity> getEntities() {
        return Stream.concat(this.occupants.stream(), this.entitySet.stream());
    }

    @Nonnull
    
    public Chunk getChunkAt(int x, int z) {
        return this.chunks.get(x, z, true);
    }

    @Nullable
    
    public Chunk getChunkAt(int x, int z, boolean gen) {
        return this.chunks.get(x, z, gen);
    }

    
    public Collection<? extends Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(this.chunks.values());
    }

    
    public int getHighestY(int x, int z) {
        return this.getChunkAt(x >> 4, z >> 4).getHighestY(x & 15, z & 15);
    }

    
    public Block getBlockAt(int x, int y, int z) {
        return new Block(new Position(this, x, y, z));
    }

    public Chunk removeChunkAt(int x, int z) {
        return this.chunks.remove(x, z);
    }

    
    public Block getBlockAt(Position pos) {
        return new Block(pos);
    }

    
    public void save() {
        Path level = this.directory.resolve("level.dat");
        Path regionDir = this.directory.resolve("region");
        try {
            if (!Files.exists(this.directory)) {
                Files.createDirectory(this.directory);
            }

            if (!Files.exists(level)) {
                Files.createFile(level);
            }

            if (!Files.exists(regionDir)) {
                Files.createDirectory(regionDir);
            }

            Compound worldRoot = new Compound("");
            Compound worldData = new Compound("Data");
            worldRoot.putCompound(worldData);

            this.worldOptions.write(worldData);
            this.generatorOptions.write(worldData);
            this.weather.write(worldData);
            this.border.write(worldData);

            worldData.putString("LevelName", this.name);

            try (GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(level.toFile()))) {
                worldRoot.write(new DataOutputStream(stream));
            }

            this.chunks.forEach(c -> {
                Region region = Region.getFile(c, true);
                try (DataOutputStream out = region.getChunkDataOutputStream(c.getX() & 31, c.getZ() & 31)) {
                    Compound rootChunk = new Compound("");
                    Compound chunkData = new Compound("Level");
                    c.write(chunkData);
                    rootChunk.putCompound(chunkData);
                    rootChunk.write(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}