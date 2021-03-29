package io.vera.server.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ServerThreadPool implements Executor {
  private static final Map<PoolSpec, ServerThreadPool> pools = new ConcurrentHashMap<>();
  
  private final ExecutorService delegate;
  
  private ServerThreadPool(ExecutorService executor) {
    this.delegate = executor;
  }
  
  public static void init() {
    forSpec(PoolSpec.WORLDS);
    forSpec(PoolSpec.CHUNKS);
    forSpec(PoolSpec.ENTITIES);
    forSpec(PoolSpec.PLAYERS);
    forSpec(PoolSpec.PLUGINS);
    forSpec(PoolSpec.SCHEDULER);
  }
  
  public static ServerThreadPool forSpec(PoolSpec spec) {
    return pools.computeIfAbsent(spec, k -> {
          int config = spec.getMaxThreads();
          return spec.isDoStealing() ? new ServerThreadPool(new ForkJoinPool(config, spec, null, true)) : new ServerThreadPool(new ThreadPoolExecutor(1, config, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), spec));
        });
  }
  
  public static void shutdownAll() {
    for (ServerThreadPool pool : pools.values())
      pool.shutdown(); 
  }
  
  public void shutdown() {
    this.delegate.shutdown();
  }
  
  public <T> Future<T> submit(Callable<T> task) {
    return this.delegate.submit(task);
  }
  
  public <T> Future<T> submit(Runnable task, T result) {
    return this.delegate.submit(task, result);
  }
  
  public Future<?> submit(Runnable task) {
    return this.delegate.submit(task);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return this.delegate.invokeAll(tasks);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return this.delegate.invokeAll(tasks);
  }
  
  public void execute(@Nonnull Runnable command) {
    this.delegate.execute(command);
  }
}
