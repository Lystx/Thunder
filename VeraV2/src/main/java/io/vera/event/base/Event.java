package io.vera.event.base;

import io.vera.event.annotations.Supertype;
import javax.annotation.concurrent.NotThreadSafe;

@Supertype
@NotThreadSafe
public class Event {

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }
}
