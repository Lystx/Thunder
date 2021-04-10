
package io.lightning.network.utility.exposed.data;

import io.lightning.network.utility.exposed.consumer.BooleanConsumer;
import io.lightning.network.utility.exposed.predicate.BooleanPredicate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public interface BooleanReader extends DataReader {
    

    default void readBoolean(BooleanConsumer consumer) {
        read(Byte.BYTES, buffer -> consumer.accept(buffer.get() == 1), ByteOrder.BIG_ENDIAN);
    }

    default void readBooleanUntil(BooleanPredicate predicate) {
        readUntil(Byte.BYTES, buffer -> predicate.test(buffer.get() == 1), ByteOrder.BIG_ENDIAN);
    }

    default void readBooleanAlways(BooleanConsumer consumer) {
        readAlways(Byte.BYTES, buffer -> consumer.accept(buffer.get() == 1), ByteOrder.BIG_ENDIAN);
    }

    default void readBooleans(int n, Consumer<boolean[]> consumer) {
        read(Byte.BYTES * n, buffer -> processBooleans(buffer, n, consumer), ByteOrder.BIG_ENDIAN);
    }

    default void readBooleansAlways(int n, Consumer<boolean[]> consumer) {
        readAlways(Byte.BYTES * n, buffer -> processBooleans(buffer, n, consumer), ByteOrder.BIG_ENDIAN);
    }

    default void processBooleans(ByteBuffer buffer, int n, Consumer<boolean[]> consumer) {
        boolean[] b = new boolean[n];
    
        for (int i = 0; i < n; i++) {
            b[i] = buffer.get() == 1;
        }
    
        consumer.accept(b);
    }
}
