
package io.vera.server.concurrent;

import lombok.Getter;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class VeraScheduler {
    private static final ServerThreadPool POOL = ServerThreadPool.forSpec(PoolSpec.SCHEDULER);

    @Getter
    private static final VeraScheduler instance = new VeraScheduler();

    private VeraScheduler() {
    }

    public void tick() {
    }
}