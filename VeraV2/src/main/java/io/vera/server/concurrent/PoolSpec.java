package io.vera.server.concurrent;

import io.vera.server.VeraServer;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class PoolSpec implements ThreadFactory, ForkJoinPool.ForkJoinWorkerThreadFactory, Thread.UncaughtExceptionHandler {
  public static final ThreadFactory UNCAUGHT_FACTORY = new PoolSpec("VRA - Net", 0, false);
  
  public static final PoolSpec WORLDS = new PoolSpec("VRA - Worlds", 4, true);
  
  public static final PoolSpec CHUNKS = new PoolSpec("VRA - Chunks", 4, true);
  
  public static final PoolSpec ENTITIES = new PoolSpec("VRA - Entities", 3, false);
  
  public static final PoolSpec PLAYERS = new PoolSpec("VRA - Players", 3, false);
  
  public static final PoolSpec SCHEDULER = new PoolSpec("VRA - Scheduler", 3, false);
  
  public static final PoolSpec PLUGINS = new PoolSpec("VRA - Plugins", 1, false);
  
  private final String name;
  
  private final int maxThreads;
  
  private final boolean doStealing;
  
  public int getMaxThreads() {
    return this.maxThreads;
  }
  
  public boolean isDoStealing() {
    return this.doStealing;
  }
  
  public PoolSpec(String name, int maxThreads, boolean doStealing) {
    this.name = name;
    this.maxThreads = maxThreads;
    this.doStealing = doStealing;
  }
  
  public Thread newThread(@Nonnull Runnable r) {
    Thread thread = new Thread(r, this.name);
    thread.setUncaughtExceptionHandler(this);
    return thread;
  }
  
  public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
    ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
    worker.setName(this.name + " - " + worker.getPoolIndex());
    worker.setUncaughtExceptionHandler(this);
    return worker;
  }
  
  public void uncaughtException(Thread t, Throwable e) {
    e.printStackTrace(new PrintStream(System.out) {
          public void println(Object x) {
            VeraServer.getInstance().getLogger().error(String.valueOf(x));
          }
        });
  }
}
