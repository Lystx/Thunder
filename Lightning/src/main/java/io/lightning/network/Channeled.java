
package io.lightning.network;

import java.io.IOException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.Channel;

@FunctionalInterface
public interface Channeled<T extends AsynchronousChannel> {

    T getChannel();

    default void close() {
        try {
            getChannel().close();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to close the backing AsynchronousChannel:", e);
        }
    }
}
