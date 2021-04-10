
package io.lightning.network.utility.exposed.consumer;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface FloatConsumer {

    void accept(float value);

    default FloatConsumer andThen(FloatConsumer after) {
        Objects.requireNonNull(after);
        return (float t) -> { accept(t); after.accept(t); };
    }
}
