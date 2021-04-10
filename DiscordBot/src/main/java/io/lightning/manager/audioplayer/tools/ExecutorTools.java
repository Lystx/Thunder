package io.lightning.manager.audioplayer.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecutorTools {
    private static final Logger log = LoggerFactory.getLogger(ExecutorTools.class);

    private static final long WAIT_TIME = 1000L;

    public static final CompletedVoidFuture COMPLETED_VOID = new CompletedVoidFuture();

    public static void shutdownExecutor(ExecutorService executorService, String description) {
        if (executorService == null) {
            return;
        }

        log.debug("Shutting down executor {}", description);

        executorService.shutdownNow();

        try {
            if (!executorService.awaitTermination(WAIT_TIME, TimeUnit.MILLISECONDS)) {
                log.debug("Executor {} did not shut down in {}", description, WAIT_TIME);
            } else {
                log.debug("Executor {} successfully shut down", description);
            }
        } catch (InterruptedException e) {
            log.debug("Received an interruption while shutting down executor {}", description);
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadPoolExecutor createEagerlyScalingExecutor(int coreSize, int maximumSize, long timeout,
                                                                  int queueCapacity, ThreadFactory threadFactory) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maximumSize, timeout, TimeUnit.MILLISECONDS,
                new EagerlyScalingTaskQueue(queueCapacity), threadFactory);

        executor.setRejectedExecutionHandler(new EagerlyScalingRejectionHandler());
        return executor;
    }

    private static class EagerlyScalingTaskQueue extends LinkedBlockingQueue<Runnable> {
        public EagerlyScalingTaskQueue(int capacity) {
            super(capacity);
        }

        @Override
        public boolean offer(Runnable runnable) {
            return isEmpty() && super.offer(runnable);
        }

        public boolean offerDirectly(Runnable runnable) {
            return super.offer(runnable);
        }
    }

    private static class EagerlyScalingRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (!((EagerlyScalingTaskQueue) executor.getQueue()).offerDirectly(runnable)) {
                throw new RejectedExecutionException("Task " + runnable.toString() + " rejected from " + runnable.toString());
            }
        }
    }

    private static class CompletedVoidFuture implements Future<Void> {
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}
