package io.vera.server.util;

import io.vera.util.Tuple;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Cache<T, M> {
  private static final int MAX_EVICTION_ITERATIONS = 100;
  
  private static final BiFunction NOP = (a, b) -> Boolean.valueOf(false);
  
  private final ConcurrentHashMap<T, Tuple<M, Long>> cache = new ConcurrentHashMap<>();
  
  private final long timeout;
  
  private final BiFunction<T, M, Boolean> expire;
  
  public Cache(long timeout) {
    this(timeout, NOP);
  }
  
  public Cache(long timeout, BiFunction<T, M, Boolean> expire) {
    this.timeout = timeout;
    this.expire = expire;
  }
  
  @Nonnull
  public M get(T key, Supplier<M> loader) {
    scan();
    Tuple<M, Long> instance = this.cache.get(key);
    if (instance == null)
      return (M)((Tuple)this.cache.computeIfAbsent(key, t -> new Tuple(loader.get(), Long.valueOf(System.currentTimeMillis())))).getA(); 
    if (System.currentTimeMillis() - ((Long)instance.getB()).longValue() > this.timeout) {
      this.cache.computeIfPresent(key, (k, v) -> ((Boolean)this.expire.apply((T)key, (M)instance.getA())).booleanValue() ? null : instance);
      return (M)((Tuple)this.cache.computeIfAbsent(key, t -> new Tuple(loader.get(), Long.valueOf(System.currentTimeMillis())))).getA();
    } 
    return (M)instance.getA();
  }
  
  public M compute(T key, BiFunction<T, M, M> loader) {
    Tuple<M, Long> compute = this.cache.compute(key, (k, v) -> {
          if (v == null) {
            M m = (M)loader.apply(k, null);
            return new Tuple(m, Long.valueOf(System.currentTimeMillis()));
          } 
          M apply = loader.apply(k, v.getA());
          return (apply == null) ? null : new Tuple(apply, Long.valueOf(System.currentTimeMillis()));
        });
    return (compute == null) ? null : (M)compute.getA();
  }
  
  @Nullable
  public M get(T key) {
    scan();
    Tuple<M, Long> instance = this.cache.get(key);
    if (instance == null)
      return null; 
    if (System.currentTimeMillis() - ((Long)instance.getB()).longValue() > this.timeout) {
      Tuple<M, Long> tuple = this.cache.computeIfPresent(key, (k, v) -> ((Boolean)this.expire.apply((T)key, (M)instance.getA())).booleanValue() ? null : instance);
      return (tuple == null) ? null : (M)tuple.getA();
    } 
    return (M)instance.getA();
  }
  
  public void put(T key, M value) {
    scan();
    this.cache.put(key, new Tuple(value, Long.valueOf(System.currentTimeMillis())));
  }
  
  private void scan() {
    long time = System.currentTimeMillis();
    int rounds = 0;
    Iterator<Map.Entry<T, Tuple<M, Long>>> it = this.cache.entrySet().stream().sorted(Comparator.comparingLong(o -> ((Long)((Tuple)o.getValue()).getB()).longValue())).filter(e -> (time - ((Long)((Tuple)e.getValue()).getB()).longValue() > this.timeout)).iterator();
    for (; it.hasNext() && rounds < 100; 
      rounds++) {
      Map.Entry<T, Tuple<M, Long>> e = it.next();
      this.cache.computeIfPresent(e.getKey(), (k, v) -> ((Boolean)this.expire.apply((T)e.getKey(), (M)((Tuple)e.getValue()).getA())).booleanValue() ? null : (Tuple)e.getValue());
    } 
  }
}
