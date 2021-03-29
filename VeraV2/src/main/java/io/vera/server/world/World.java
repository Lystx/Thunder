package io.vera.server.world;

import io.vera.entity.Entity;
import io.vera.entity.living.Player;
import io.vera.meta.nbt.Compound;
import io.vera.meta.nbt.Tag;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.entity.VeraEntity;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutTime;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.opt.GenOptImpl;
import io.vera.server.world.opt.WeatherImpl;
import io.vera.server.world.opt.WorldBorderImpl;
import io.vera.server.world.opt.WorldOptImpl;
import io.vera.world.opt.Dimension;
import io.vera.world.opt.WorldCreateSpec;
import io.vera.world.other.Position;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class World {
  private static final ServerThreadPool TP = ServerThreadPool.forSpec(PoolSpec.WORLDS);
  
  private final Runnable tickingTask = this::doTick;
  
  private final ChunkMap chunks = new ChunkMap(this);
  
  private final String name;
  
  private final Path directory;
  
  private final Dimension dimension;
  
  private final WorldOptImpl worldOptions;
  
  private final GenOptImpl generatorOptions;
  
  public ChunkMap getChunks() {
    return this.chunks;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Path getDirectory() {
    return this.directory;
  }
  
  public Dimension getDimension() {
    return this.dimension;
  }
  
  public WorldOptImpl getWorldOptions() {
    return this.worldOptions;
  }
  
  public GenOptImpl getGeneratorOptions() {
    return this.generatorOptions;
  }
  
  private final WorldBorderImpl border = new WorldBorderImpl(this);
  
  public WorldBorderImpl getBorder() {
    return this.border;
  }
  
  private final WeatherImpl weather = new WeatherImpl(this);
  
  public WeatherImpl getWeather() {
    return this.weather;
  }
  
  private final AtomicInteger time = new AtomicInteger();
  
  private final LongAdder age = new LongAdder();
  
  public LongAdder getAge() {
    return this.age;
  }
  
  public Set<VeraPlayer> getOccupants() {
    return this.occupants;
  }
  
  private final Set<VeraPlayer> occupants = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public Set<VeraEntity> getEntitySet() {
    return this.entitySet;
  }
  
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
      for (int z = centerZ - radius; z < centerZ + radius; z++)
        getChunkAt(x, z); 
    } 
  }
  
  public final void tick() {
    TP.execute(this.tickingTask);
  }
  
  private void doTick() {
    int curTime, newTime;
    this.age.increment();
    do {
      curTime = this.time.get();
      newTime = curTime + 1;
      if (newTime != 24000)
        continue; 
      newTime = 0;
    } while (!this.time.compareAndSet(curTime, newTime));
    if (newTime == 0)
      RecipientSelector.inWorld(this, new PacketOut[] { (PacketOut)new PlayOutTime(this.age.longValue(), newTime) }); 
    this.weather.tick();
    this.border.tick();
    this.chunks.forEach(Chunk::tick);
  }
  
  public int getTime() {
    return this.time.get();
  }
  
  public Set<? extends Player> getPlayers() {
    return (Set)Collections.unmodifiableSet(this.occupants);
  }
  
  public Stream<? extends Entity> getEntities() {
    return Stream.concat((Stream)this.occupants.stream(), (Stream)this.entitySet.stream());
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
    return getChunkAt(x >> 4, z >> 4).getHighestY(x & 0xF, z & 0xF);
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
      if (!Files.exists(this.directory, new java.nio.file.LinkOption[0]))
        Files.createDirectory(this.directory, (FileAttribute<?>[])new FileAttribute[0]); 
      if (!Files.exists(level, new java.nio.file.LinkOption[0]))
        Files.createFile(level, (FileAttribute<?>[])new FileAttribute[0]); 
      if (!Files.exists(regionDir, new java.nio.file.LinkOption[0]))
        Files.createDirectory(regionDir, (FileAttribute<?>[])new FileAttribute[0]); 
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
            try (DataOutputStream out = region.getChunkDataOutputStream(c.getX() & 0x1F, c.getZ() & 0x1F)) {
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
