
package io.lightning.network.utility.exposed.consumer;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface ShortConsumer {

    void accept(short value);

    default ShortConsumer andThen(ShortConsumer after) {
        Objects.requireNonNull(after);
        return (short t) -> { accept(t); after.accept(t); };
    }
}
