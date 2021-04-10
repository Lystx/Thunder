
package io.lightning.network.utility.exposed.data;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface StringReader extends ShortReader {

    default void readString(Consumer<String> consumer) {
        readString(consumer, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
    }

    default void readString(Consumer<String> consumer, Charset charset) {
        readString(consumer, charset, ByteOrder.BIG_ENDIAN);
    }

    default void readString(Consumer<String> consumer, Charset charset, ByteOrder order) {
        readShort(length -> processBytes(length, consumer, charset, order));
    }

    default void readStringUntil(Predicate<String> predicate) {
        readStringUntil(predicate, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
    }

    default void readStringUntil(Predicate<String> predicate, Charset charset) {
        readStringUntil(predicate, charset, ByteOrder.BIG_ENDIAN);
    }

    default void readStringUntil(Predicate<String> predicate, Charset charset, ByteOrder order) {
        readShortUntil(length -> {
            boolean[] toReturn = new boolean[1];
            processBytes(length, string -> toReturn[0] = predicate.test(string), charset, order);
            return toReturn[0];
        });
    }

    default void readStringAlways(Consumer<String> consumer) {
        readStringAlways(consumer, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
    }

    default void readStringAlways(Consumer<String> consumer, Charset charset) {
        readStringAlways(consumer, charset, ByteOrder.BIG_ENDIAN);
    }

    default void readStringAlways(Consumer<String> consumer, Charset charset, ByteOrder order) {
        readShortAlways(length -> processBytes(length, consumer, charset, order));
    }

    default void processBytes(short n, Consumer<String> consumer, Charset charset, ByteOrder order) {
        int length = order == ByteOrder.LITTLE_ENDIAN ? Short.reverseBytes(n) : n;
        
        read(Byte.BYTES * (length & 0xFFFF), buffer -> {
            byte[] b = new byte[length];
            buffer.get(b);
            consumer.accept(new String(b, charset));
        }, order);
    }
}
