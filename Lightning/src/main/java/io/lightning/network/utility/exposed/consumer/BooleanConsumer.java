
package io.lightning.network.utility.exposed.consumer;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface BooleanConsumer {

    void accept(boolean value);

    default BooleanConsumer andThen(BooleanConsumer after) {
        Objects.requireNonNull(after);
        return (boolean t) -> { accept(t); after.accept(t); };
    }
}
