package io.vera.server.world.gen;

import io.vera.inventory.Substance;
import io.vera.server.util.UncheckedCdl;
import io.vera.server.world.ChunkSection;
import io.vera.world.gen.GeneratorContext;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class GeneratorContextImpl implements GeneratorContext {
  private final Executor container;
  
  private final LongAdder count = new LongAdder();
  
  private final Queue<Consumer<UncheckedCdl>> tasks = new ConcurrentLinkedQueue<>();
  
  private final long seed;
  
  private final boolean doSkylight;
  
  private final AtomicLong random;
  
  private final AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(16);
  
  private final AtomicIntegerArray maxY = new AtomicIntegerArray(256);
  
  public GeneratorContextImpl(Executor container, long seed, boolean doSkylight) {
    this.container = container;
    this.seed = seed;
    this.doSkylight = doSkylight;
    this.random = new AtomicLong(seed);
  }
  
  public long nextLong() {
    while (true) {
      long l = this.random.get();
      long x = l;
      x ^= x << 21L;
      x ^= x >>> 35L;
      x ^= x << 4L;
      if (x != 0L && this.random.compareAndSet(l, x))
        return x; 
    } 
  }
  
  public long nextLong(long max) {
    return nextLong() % max;
  }
  
  public int nextInt() {
    return nextInt(2147483647);
  }
  
  public int nextInt(int max) {
    return (int)nextLong() % max;
  }
  
  public long seed() {
    return this.seed;
  }
  
  public int maxHeight(int x, int z) {
    return this.maxY.get(x << 4 | z & 0xF);
  }
  
  public void set(int x, int y, int z, Substance substance, byte meta) {
    set(x, y, z, build(substance.getId(), meta));
  }
  
  public void set(int x, int y, int z, Substance substance) {
    set(x, y, z, build(substance.getId(), (byte)0));
  }
  
  public void set(int x, int y, int z, int id, byte meta) {
    set(x, y, z, build(id, meta));
  }
  
  public void run(Runnable r) {
    this.count.increment();
    this.tasks.offer(cdl -> {
          r.run();
          cdl.countDown();
        });
  }
  
  public void doRun(UncheckedCdl latch) {
    for (Consumer<UncheckedCdl> consumer : this.tasks)
      this.container.execute(() -> consumer.accept(latch)); 
  }
  
  public UncheckedCdl getCount() {
    return new UncheckedCdl(this.count.intValue());
  }
  
  public void reset() {
    this.count.reset();
    this.tasks.clear();
  }
  
  public void copySections(AtomicReferenceArray<ChunkSection> sections) {
    for (int i = 0; i < this.sections.length(); i++)
      sections.set(i, this.sections.get(i)); 
  }
  
  public void copyHeights(AtomicIntegerArray array) {
    for (int i = 0; i < array.length(); i++)
      array.set(i, this.maxY.get(i)); 
  }
  
  private void set(int x, int y, int z, short state) {
    int lastMax, sectionIdx = section(y);
    int idx = idx(x, y & 0xF, z);
    int xz = x << 4 | z & 0xF;
    ChunkSection section = this.sections.get(sectionIdx);
    if (section == null) {
      ChunkSection newSec = new ChunkSection(this.doSkylight);
      if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
        section = newSec;
      } else {
        section = this.sections.get(sectionIdx);
      } 
    } 
    do {
      lastMax = this.maxY.get(xz);
      if (y <= lastMax)
        break; 
    } while (!this.maxY.compareAndSet(xz, lastMax, y));
    section.set(idx, state);
  }
  
  private static short build(int id, byte meta) {
    return (short)(id << 4 | meta);
  }
  
  private static int idx(int x, int y, int z) {
    return y << 8 | z << 4 | x;
  }
  
  private static int section(int y) {
    return y >> 4;
  }
}
