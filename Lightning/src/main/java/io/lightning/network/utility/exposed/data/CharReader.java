
package io.lightning.network.utility.exposed.data;

import io.lightning.network.utility.exposed.consumer.CharConsumer;
import io.lightning.network.utility.exposed.predicate.CharPredicate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public interface CharReader extends DataReader {

    default void readChar(CharConsumer consumer) {
        readChar(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readChar(CharConsumer consumer, ByteOrder order) {
        read(Character.BYTES, buffer -> consumer.accept(buffer.getChar()), order);
    }

    default void readCharUntil(CharPredicate predicate) {
        readCharUntil(predicate, ByteOrder.BIG_ENDIAN);
    }

    default void readCharUntil(CharPredicate predicate, ByteOrder order) {
        readUntil(Character.BYTES, buffer -> predicate.test(buffer.getChar()), order);
    }

    default void readCharAlways(CharConsumer consumer) {
        readCharAlways(consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readCharAlways(CharConsumer consumer, ByteOrder order) {
        readAlways(Character.BYTES, buffer -> consumer.accept(buffer.getChar()), order);
    }

    default void readChars(int n, Consumer<char[]> consumer) {
        readChars(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readChars(int n, Consumer<char[]> consumer, ByteOrder order) {
        read(Character.BYTES * n, buffer -> processChars(buffer, n, consumer), order);
    }

    default void readCharsAlways(int n, Consumer<char[]> consumer) {
        readCharsAlways(n, consumer, ByteOrder.BIG_ENDIAN);
    }

    default void readCharsAlways(int n, Consumer<char[]> consumer, ByteOrder order) {
        readAlways(Character.BYTES * n, buffer -> processChars(buffer, n, consumer), order);
    }

    default void processChars(ByteBuffer buffer, int n, Consumer<char[]> consumer) {
        char[] c = new char[n];
        buffer.asCharBuffer().get(c);
        consumer.accept(c);
    }
}
