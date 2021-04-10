package io.lightning.manager.audioplayer.natives;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class NativeResourceHolder {
    private static final Logger log = LoggerFactory.getLogger(NativeResourceHolder.class);

    private final AtomicBoolean released = new AtomicBoolean();

    protected void checkNotReleased() {
        if (released.get()) {
            throw new IllegalStateException("Cannot use the decoder after closing it.");
        }
    }

    public void close() {
        closeInternal(false);
    }


    protected abstract void freeResources();

    private synchronized void closeInternal(boolean inFinalizer) {
        if (released.compareAndSet(false, true)) {
            if (inFinalizer) {
                log.warn("Should have been closed before finalization ({}).", this.getClass().getName());
            }

            freeResources();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeInternal(true);
    }
}
