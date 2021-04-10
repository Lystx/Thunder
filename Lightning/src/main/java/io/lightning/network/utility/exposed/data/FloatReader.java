
package io.lightning.network.utility.exposed.data;

import io.lightning.network.utility.exposed.consumer.FloatConsumer;
import io.lightning.network.utility.exposed.predicate.FloatPredicate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public interface FloatReader extends DataReader {

    default void readFloat(FloatConsumer consumer) {
        readFloat(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readFloat(FloatConsumer consumer, ByteOrder order) {
        read(Float.BYTES, buffer -> consumer.accept(buffer.getFloat()), order);
    }

    default void readFloatUntil(FloatPredicate predicate) {
        readFloatUntil(predicate, ByteOrder.BIG_ENDIAN);
    }

    default void readFloatUntil(FloatPredicate predicate, ByteOrder order) {
        readUntil(Float.BYTES, buffer -> predicate.test(buffer.getFloat()), order);
    }

    default void readFloatAlways(FloatConsumer consumer) {
        readFloatAlways(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readFloatAlways(FloatConsumer consumer, ByteOrder order) {
        readAlways(Float.BYTES, buffer -> consumer.accept(buffer.getFloat()), order);
    }

    default void readFloats(int n, Consumer<float[]> consumer) {
        readFloats(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readFloats(int n, Consumer<float[]> consumer, ByteOrder order) {
        read(Float.BYTES * n, buffer -> processFloats(buffer, n, consumer), order);
    }

    default void readFloatsAlways(int n, Consumer<float[]> consumer) {
        readFloatsAlways(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readFloatsAlways(int n, Consumer<float[]> consumer, ByteOrder order) {
        readAlways(Float.BYTES * n, buffer -> processFloats(buffer, n, consumer), order);
    }

    default void processFloats(ByteBuffer buffer, int n, Consumer<float[]> consumer) {
        float[] f = new float[n];
        buffer.asFloatBuffer().get(f);
        consumer.accept(f);
    }
}
