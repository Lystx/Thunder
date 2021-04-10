
package io.lightning.network.utility.exposed.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;


public interface IntReader extends DataReader {
    

    default void readInt(IntConsumer consumer) {
        readInt(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readInt(IntConsumer consumer, ByteOrder order) {
        read(Integer.BYTES, buffer -> consumer.accept(buffer.getInt()), order);
    }

    default void readIntUntil(IntPredicate predicate) {
        readIntUntil(predicate, ByteOrder.BIG_ENDIAN);
    }

    default void readIntUntil(IntPredicate predicate, ByteOrder order) {
        readUntil(Integer.BYTES, buffer -> predicate.test(buffer.getInt()), order);
    }

    default void readIntAlways(IntConsumer consumer) {
        readIntAlways(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readIntAlways(IntConsumer consumer, ByteOrder order) {
        readAlways(Integer.BYTES, buffer -> consumer.accept(buffer.getInt()), order);
    }

    default void readInts(int n, Consumer<int[]> consumer) {
        readInts(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readInts(int n, Consumer<int[]> consumer, ByteOrder order) {
        read(Integer.BYTES * n, buffer -> processInts(buffer, n, consumer), order);
    }

    default void readIntsAlways(int n, Consumer<int[]> consumer) {
        readIntsAlways(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readIntsAlways(int n, Consumer<int[]> consumer, ByteOrder order) {
        readAlways(Integer.BYTES * n, buffer -> processInts(buffer, n, consumer), order);
    }

    default void processInts(ByteBuffer buffer, int n, Consumer<int[]> consumer) {
        int[] i = new int[n];
        buffer.asIntBuffer().get(i);
        consumer.accept(i);
    }
}
