
package io.lightning.network.utility.exposed.consumer;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface CharConsumer {

    void accept(char value);

    default CharConsumer andThen(CharConsumer after) {
        Objects.requireNonNull(after);
        return (char t) -> { accept(t); after.accept(t); };
    }
}
