package io.vera.server.world;

import io.netty.buffer.ByteBuf;
import io.vera.entity.Entity;
import io.vera.entity.living.Player;
import io.vera.meta.nbt.Compound;
import io.vera.meta.nbt.Tag;
import io.vera.meta.nbt.TagList;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.player.VeraPlayer;
import io.vera.server.util.UncheckedCdl;
import io.vera.server.world.gen.GeneratorContextImpl;
import io.vera.server.world.opt.GenOptImpl;
import io.vera.world.gen.FeatureGenerator;
import io.vera.world.gen.GenContainer;
import io.vera.world.gen.GeneratorContext;
import io.vera.world.gen.GeneratorProvider;
import io.vera.world.gen.PropGenerator;
import io.vera.world.gen.TerrainGenerator;
import io.vera.world.opt.Dimension;
import io.vera.world.other.Position;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Chunk {
  private static final ServerThreadPool ARBITRARY_POOL = ServerThreadPool.forSpec(PoolSpec.CHUNKS);
  
  private static final ServerThreadPool DEFAULT_POOL = ServerThreadPool.forSpec(PoolSpec.PLUGINS);
  
  private static final int USABLE = -1;
  
  private static final int TRANSITION = 0;
  
  private static final int UNUSABLE = 1;
  
  private final AtomicInteger useState = new AtomicInteger(-1);
  
  private final AtomicBoolean generationInProgress = new AtomicBoolean();
  
  private final UncheckedCdl ready = new UncheckedCdl(1);
  
  private final World world;
  
  private final int x;
  
  private final int z;
  
  private final ChunkSection emptyPlaceholder;
  
  public World getWorld() {
    return this.world;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getZ() {
    return this.z;
  }
  
  private final AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(16);
  
  private final AtomicIntegerArray heights = new AtomicIntegerArray(256);
  
  private final LongAdder inhabited = new LongAdder();
  
  public Set<VeraPlayer> getOccupants() {
    return this.occupants;
  }
  
  private final Set<VeraPlayer> occupants = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public Set<VeraPlayer> getHolders() {
    return this.holders;
  }
  
  private final Set<VeraPlayer> holders = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public Set<VeraEntity> getEntitySet() {
    return this.entitySet;
  }
  
  private final Set<VeraEntity> entitySet = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public Chunk(World world, int x, int z) {
    this.world = world;
    this.x = x;
    this.z = z;
    if (world.getDimension() == Dimension.OVERWORLD) {
      this.emptyPlaceholder = ChunkSection.EMPTY_WITH_SKYLIGHT;
    } else {
      this.emptyPlaceholder = ChunkSection.EMPTY_WITHOUT_SKYLIGHT;
    } 
  }
  
  public void tick() {
    ARBITRARY_POOL.execute(() -> {
          this.inhabited.add(this.occupants.size());
          if (this.world.getTime() == 0)
            checkValidForGc(); 
        });
  }
  
  public void generate() {
    if (!this.generationInProgress.compareAndSet(false, true))
      return; 
    Region region = Region.getFile(this, false);
    if (region == null) {
      runGenerator();
    } else {
      int rX = this.x & 0x1F;
      int rZ = this.z & 0x1F;
      if (region.hasChunk(rX, rZ)) {
        try (DataInputStream in = region.getChunkDataInputStream(rX, rZ)) {
          Compound compound = Tag.decode(in).getCompound("Level");
          CompletableFuture.runAsync(() -> read(compound), (Executor)ARBITRARY_POOL)
            .whenCompleteAsync((v, t) -> {
                if (this.ready.getCount() == 1L)
                  runGenerator(); 
              }(Executor)ARBITRARY_POOL);
          waitReady();
        } catch (IOException e) {
          throw new RuntimeException(e);
        } 
      } else {
        runGenerator();
      } 
    } 
  }
  
  private void runGenerator() {
    ServerThreadPool serverThreadPool;
    GenOptImpl genOptImpl = this.world.getGeneratorOptions();
    GeneratorProvider provider = genOptImpl.getProvider();
    GenContainer genContainer = provider.getGenerationContainer();
    if (genContainer == GenContainer.DEFAULT) {
      serverThreadPool = DEFAULT_POOL;
    } else if (serverThreadPool == GenContainer.ARBITRARY) {
      serverThreadPool = ARBITRARY_POOL;
    } 
    TerrainGenerator terrain = provider.getTerrainGenerator(this.world);
    Set<PropGenerator> props = provider.getPropGenerators(this.world);
    Set<FeatureGenerator> features = provider.getFeatureGenerators(this.world);
    GeneratorContextImpl context = new GeneratorContextImpl((Executor)serverThreadPool, genOptImpl.getSeed(), (this.world.getDimension() == Dimension.OVERWORLD));
    CompletableFuture.supplyAsync(() -> {
          terrain.generate(this.x, this.z, (GeneratorContext)context);
          for (FeatureGenerator generator : features)
            generator.generate(this.x, this.z, (GeneratorContext)context); 
          UncheckedCdl latch = context.getCount();
          context.doRun(latch);
          return latch;
        }(Executor)serverThreadPool)
      
      .thenApplyAsync(l -> {
          l.await();
          context.reset();
          for (PropGenerator generator : props)
            generator.generate(this.x, this.z, (GeneratorContext)context); 
          UncheckedCdl latch = context.getCount();
          context.doRun(latch);
          return latch;
        }(Executor)serverThreadPool).thenAcceptAsync(l -> {
          l.await();
          context.copySections(this.sections);
          context.copyHeights(this.heights);
          this.ready.countDown();
        }(Executor)serverThreadPool);
    waitReady();
  }
  
  public Chunk waitReady() {
    this.ready.await();
    return this;
  }
  
  public void write(ByteBuf buf, boolean continuous) {
    int len = this.sections.length();
    short mask = 0;
    ChunkSection[] sections = new ChunkSection[16];
    for (int i = 0; i < 16; i++) {
      ChunkSection sec = this.sections.get(i);
      sections[i] = sec;
      if (sec != null)
        mask = (short)(mask | 1 << i); 
    } 
    NetData.wvint(buf, mask);
    ByteBuf chunkData = buf.alloc().buffer();
    try {
      for (int j = 0; j < len; j++) {
        if ((mask & 1 << j) == 1 << j) {
          ChunkSection sec = sections[j];
          if (sec != null) {
            sec.write(chunkData);
          } else {
            this.emptyPlaceholder.write(chunkData);
          } 
        } 
      } 
      NetData.wvint(buf, chunkData.readableBytes() + (continuous ? 256 : 0));
      buf.writeBytes(chunkData);
    } finally {
      chunkData.release();
    } 
    if (continuous)
      for (int j = 0; j < 256; j++)
        buf.writeByte(1);  
    NetData.wvint(buf, 0);
  }
  
  @Nonnull
  public Block getBlockAt(int x, int y, int z) {
    return new Block(new Position(this.world, this.x << 4 + x, y, this.z << 4 + z));
  }
  
  public Set<? extends Player> getPlayers() {
    return (Set)Collections.unmodifiableSet(this.occupants);
  }
  
  public Stream<? extends Entity> getEntities() {
    return Stream.concat((Stream)this.occupants.stream(), (Stream)this.entitySet.stream());
  }
  
  public boolean canUse() {
    int state;
    do {
      state = this.useState.get();
    } while (state == 0);
    return (state == -1);
  }
  
  public void checkValidForGc() {
    this.useState.set(0);
    int centerX = this.world.getWorldOptions().getSpawn().getIntX() >> 4;
    int centerZ = this.world.getWorldOptions().getSpawn().getIntZ() >> 4;
    if (this.holders.isEmpty() && (Math.abs(centerX - this.x) > 3 || Math.abs(centerZ - this.z) > 3)) {
      this.useState.set(1);
      if (this.world.removeChunkAt(this.x, this.z) != null) {
        Region region = Region.getFile(this, true);
        int rX = this.x & 0x1F;
        int rZ = this.z & 0x1F;
        if (region.hasChunk(rX, rZ)) {
          try (DataInputStream in = region.getChunkDataInputStream(rX, rZ)) {
            Compound root = Tag.decode(in);
            Compound compound = root.getCompound("Level");
            CompletableFuture.runAsync(() -> write(compound), (Executor)ARBITRARY_POOL)
              .whenCompleteAsync((v, t) -> {
                  try (DataOutputStream out = region.getChunkDataOutputStream(rX, rZ)) {
                    root.write(out);
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  } 
                }(Executor)ARBITRARY_POOL);
          } catch (IOException e) {
            throw new RuntimeException(e);
          } 
        } else {
          ARBITRARY_POOL.execute(() -> {
                Compound root = new Compound("");
                Compound level = new Compound("Level");
                root.putCompound(level);
                write(level);
                try (DataOutputStream out = region.getChunkDataOutputStream(rX, rZ)) {
                  root.write(out);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                } 
              });
        } 
      } 
    } else {
      this.useState.set(-1);
    } 
  }
  
  public int getHighestY(int x, int z) {
    return this.heights.get(x << 4 | z & 0xF);
  }
  
  public short get(int x, int y, int z) {
    ChunkSection section = this.sections.get(y >> 4);
    if (section == null)
      return 0; 
    return section.dataAt((y & 0xF) << 8 | z << 4 | x);
  }
  
  public void set(int x, int y, int z, short state) {
    int height, newHeight, sectionIdx = y >> 4;
    ChunkSection section = this.sections.get(sectionIdx);
    if (section == null) {
      ChunkSection newSec = new ChunkSection((this.world.getDimension() == Dimension.OVERWORLD));
      if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
        section = newSec;
      } else {
        section = this.sections.get(sectionIdx);
      } 
    } 
    int heightIdx = x << 4 | z & 0xF;
    do {
      height = this.heights.get(heightIdx);
      newHeight = height;
      if (y > height) {
        newHeight = height;
      } else {
        for (int i = height; i >= 0; i--) {
          if (get(x, i, z) >> 4 != 0) {
            newHeight = i;
            break;
          } 
        } 
      } 
    } while (!this.heights.compareAndSet(heightIdx, height, newHeight));
    section.set((y & 0xF) << 8 | z << 4 | x, state);
  }
  
  public void read(Compound compound) {
    this.inhabited.add(compound.getLong("InhabitedTime"));
    TagList<Compound> sectionTagList = compound.getList("Sections");
    for (Compound c : sectionTagList) {
      ChunkSection section = new ChunkSection((this.world.getDimension() == Dimension.OVERWORLD));
      section.read(c);
      byte y = c.getByte("Y");
      this.sections.set(y, section);
    } 
    int[] heightMap = compound.getIntArray("HeightMap");
    for (int i = 0; i < heightMap.length; i++)
      this.heights.set(i, heightMap[i]); 
    if (compound.getByte("TerrainPopulated") == 1) {
      this.generationInProgress.set(true);
      this.ready.countDown();
    } 
  }
  
  public void write(Compound compound) {
    compound.putInt("xPos", this.x);
    compound.putInt("zPos", this.z);
    byte hasGenerated = (byte)((this.ready.getCount() == 0L) ? 1 : 0);
    compound.putByte("TerrainPopulated", hasGenerated);
    compound.putByte("LightPopulated", hasGenerated);
    compound.putLong("InhabitedTime", this.inhabited.longValue());
    compound.putLong("LastUpdate", this.world.getTime());
    TagList<Compound> sectionTagList = new TagList(Tag.Type.COMPOUND);
    for (int i = 0; i < this.sections.length(); i++) {
      ChunkSection section = this.sections.get(i);
      if (section != null) {
        Compound sectionCompound = new Compound("");
        sectionCompound.putByte("Y", (byte)i);
        section.write(sectionCompound);
        sectionTagList.add(sectionCompound);
      } 
    } 
    compound.putList("Sections", sectionTagList);
    int[] heightMap = new int[this.heights.length()];
    for (int j = 0; j < heightMap.length; j++)
      heightMap[j] = this.heights.get(j); 
    compound.putIntArray("HeightMap", heightMap);
  }
}
