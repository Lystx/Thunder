
package io.lightning.network.utility.exposed.consumer;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface ByteConsumer {

    void accept(byte value);

    default ByteConsumer andThen(ByteConsumer after) {
        Objects.requireNonNull(after);
        return (byte t) -> { accept(t); after.accept(t); };
    }
}
