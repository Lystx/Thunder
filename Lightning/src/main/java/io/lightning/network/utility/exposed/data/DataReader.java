
package io.lightning.network.utility.exposed.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;
import java.util.function.Predicate;

@FunctionalInterface
public interface DataReader {

    default void read(int n, Consumer<ByteBuffer> consumer) {
        read(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void read(int n, Consumer<ByteBuffer> consumer, ByteOrder order) {
        readUntil(n, buffer -> {
            consumer.accept(buffer);
            return false;
        }, order);
    }

    void readUntil(int n, Predicate<ByteBuffer> predicate, ByteOrder order);

    default void readAlways(int n, Consumer<ByteBuffer> consumer, ByteOrder order) {
        readUntil(n, buffer -> {
            consumer.accept(buffer);
            return true;
        }, order);
    }
}
