
package io.lightning.network.utility.exposed.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

public interface LongReader extends DataReader {

    default void readLong(LongConsumer consumer) {
        readLong(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readLong(LongConsumer consumer, ByteOrder order) {
        read(Long.BYTES, buffer -> consumer.accept(buffer.getLong()), order);
    }

    default void readLongUntil(LongPredicate predicate) {
        readLongUntil(predicate, ByteOrder.BIG_ENDIAN);
    }

    default void readLongUntil(LongPredicate predicate, ByteOrder order) {
        readUntil(Long.BYTES, buffer -> predicate.test(buffer.getLong()), order);
    }

    default void readLongAlways(LongConsumer consumer) {
        readLongAlways(consumer, ByteOrder.BIG_ENDIAN);
    }
    

    default void readLongAlways(LongConsumer consumer, ByteOrder order) {
        readAlways(Long.BYTES, buffer -> consumer.accept(buffer.getLong()), order);
    }

    default void readLongs(int n, Consumer<long[]> consumer) {
        readLongs(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readLongs(int n, Consumer<long[]> consumer, ByteOrder order) {
        read(Long.BYTES * n, buffer -> processLongs(buffer, n, consumer), order);
    }

    default void readLongsAlways(int n, Consumer<long[]> consumer) {
        readLongsAlways(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readLongsAlways(int n, Consumer<long[]> consumer, ByteOrder order) {
        readAlways(Long.BYTES * n, buffer -> processLongs(buffer, n, consumer), order);
    }

    default void processLongs(ByteBuffer buffer, int n, Consumer<long[]> consumer) {
        long[] l = new long[n];
        buffer.asLongBuffer().get(l);
        consumer.accept(l);
    }
}
