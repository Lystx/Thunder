package io.vera.server.concurrent;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class VeraScheduler {
  private static final ServerThreadPool POOL = ServerThreadPool.forSpec(PoolSpec.SCHEDULER);
  
  public static VeraScheduler getInstance() {
    return instance;
  }
  
  private static final VeraScheduler instance = new VeraScheduler();
  
  public void tick() {}
}
