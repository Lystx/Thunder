
package io.lightning.network.utility.exposed.data;

import io.lightning.network.utility.exposed.consumer.ShortConsumer;
import io.lightning.network.utility.exposed.predicate.ShortPredicate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public interface ShortReader extends DataReader {

    default void readShort(ShortConsumer consumer) {
        readShort(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readShort(ShortConsumer consumer, ByteOrder order) {
        read(Short.BYTES, buffer -> consumer.accept(buffer.getShort()), order);
    }

    default void readShortUntil(ShortPredicate predicate) {
        readShortUntil(predicate, ByteOrder.BIG_ENDIAN);
    }

    default void readShortUntil(ShortPredicate predicate, ByteOrder order) {
        readUntil(Short.BYTES, buffer -> predicate.test(buffer.getShort()), order);
    }

    default void readShortAlways(ShortConsumer consumer) {
        readShortAlways(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readShortAlways(ShortConsumer consumer, ByteOrder order) {
        readAlways(Short.BYTES, buffer -> consumer.accept(buffer.getShort()), order);
    }

    default void readShorts(int n, Consumer<short[]> consumer) {
        readShorts(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readShorts(int n, Consumer<short[]> consumer, ByteOrder order) {
        read(Short.BYTES * n, buffer -> processShorts(buffer, n, consumer), order);
    }

    default void readShortsAlways(int n, Consumer<short[]> consumer) {
        readShortsAlways(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readShortsAlways(int n, Consumer<short[]> consumer, ByteOrder order) {
        readAlways(Short.BYTES * n, buffer -> processShorts(buffer, n, consumer), order);
    }

    default void processShorts(ByteBuffer buffer, int n, Consumer<short[]> consumer) {
        short[] s = new short[n];
        buffer.asShortBuffer().get(s);
        consumer.accept(s);
    }
}
