package io.vera.server.world;

import io.vera.server.util.Long2ReferenceOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

public class ChunkMap implements Iterable<Chunk> {
  private final Object lock = new Object();
  
  @GuardedBy("lock")
  private final Long2ReferenceOpenHashMap<Chunk> chunks = new Long2ReferenceOpenHashMap();
  
  private final World world;
  
  public ChunkMap(World world) {
    this.world = world;
  }
  
  public Chunk get(int x, int z, boolean gen) {
    Chunk chunk;
    long key = x << 32L | z & 0xFFFFFFFFL;
    boolean doGenerate = false;
    synchronized (this.lock) {
      chunk = (Chunk)this.chunks.get(key);
      if ((chunk == null || !chunk.canUse()) && gen) {
        chunk = new Chunk(this.world, x, z);
        this.chunks.put(key, chunk);
        doGenerate = true;
      } 
    } 
    if (doGenerate)
      chunk.generate(); 
    if (chunk != null)
      return chunk.waitReady(); 
    return null;
  }
  
  public Chunk remove(int x, int z) {
    long key = x << 32L | z & 0xFFFFFFFFL;
    synchronized (this.lock) {
      return (Chunk)this.chunks.remove(key);
    } 
  }
  
  public Collection<Chunk> values() {
    synchronized (this.lock) {
      return this.chunks.values();
    } 
  }
  
  @Nonnull
  public Iterator<Chunk> iterator() {
    return values().iterator();
  }
  
  public void forEach(Consumer<? super Chunk> action) {
    synchronized (this.lock) {
      for (Chunk chunk : this)
        action.accept(chunk); 
    } 
  }
}
