
package io.lightning.network.utility.exposed.data;

import io.lightning.network.utility.exposed.consumer.ByteConsumer;
import io.lightning.network.utility.exposed.predicate.BytePredicate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public interface ByteReader extends DataReader {

    default void handleByte(ByteConsumer consumer) {
        read(Byte.BYTES, buffer -> consumer.accept(buffer.get()), ByteOrder.BIG_ENDIAN);
    }

    default void handleIncomingBytesUntil(BytePredicate predicate) {
        readUntil(Byte.BYTES, buffer -> predicate.test(buffer.get()), ByteOrder.BIG_ENDIAN);
    }

    default void handleIncomingBytes(ByteConsumer consumer) {
        readAlways(Byte.BYTES, buffer -> consumer.accept(buffer.get()), ByteOrder.BIG_ENDIAN);
    }

    default void handleBytes(int n, Consumer<byte[]> consumer) {
        read(Byte.BYTES * n, buffer -> processBytes(buffer, n, consumer), ByteOrder.BIG_ENDIAN);
    }
    

    default void handleIncomingBytes(int required, Consumer<byte[]> consumer) {
        readAlways(Byte.BYTES * required, buffer -> processBytes(buffer, required, consumer), ByteOrder.BIG_ENDIAN);
    }

    default void processBytes(ByteBuffer buffer, int n, Consumer<byte[]> consumer) {
        byte[] b = new byte[n];
        buffer.get(b);
        consumer.accept(b);
    }
}
