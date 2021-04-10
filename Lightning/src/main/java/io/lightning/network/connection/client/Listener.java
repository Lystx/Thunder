package io.lightning.network.connection.client;

import io.lightning.network.utility.pair.IntegerPair;
import io.lightning.network.utility.pair.Pair;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.Predicate;

public class Listener implements CompletionHandler<Integer, Pair<LightningClient, ByteBuffer>> {

    public static final Listener INSTANCE = new Listener();

    @Override
    public void completed(Integer result, Pair<LightningClient, ByteBuffer> pair) {
        int bytesReceived = result;

        if (bytesReceived == -1) {
            pair.getKey().close(false);
            return;
        }

        LightningClient lightningClient = pair.getKey();
        ByteBuffer buffer = (ByteBuffer) pair.getValue().flip();

        synchronized (lightningClient.getQueue()) {
            Deque<IntegerPair<Predicate<ByteBuffer>>> queue = lightningClient.getQueue();

            IntegerPair<Predicate<ByteBuffer>> peek;

            if ((peek = queue.peekLast()) == null) {
                lightningClient.getReadInProgress().set(false);
                return;
            }

            Deque<IntegerPair<Predicate<ByteBuffer>>> stack = lightningClient.getStack();

            boolean shouldDecrypt = lightningClient.getDecryptionCipher() != null;
            boolean queueIsEmpty = false;

            int key;

            lightningClient.getInCallback().set(true);

            while (buffer.remaining() >= (key = peek.getKey())) {
                ByteBuffer wrappedBuffer = (ByteBuffer) buffer.duplicate().mark().limit(buffer.position() + key);

                if (shouldDecrypt) {
                    try {
                        wrappedBuffer = (ByteBuffer) lightningClient.getDecryptionFunction().apply(lightningClient.getDecryptionCipher(), wrappedBuffer).reset();
                    } catch (Exception e) {
                        throw new IllegalStateException("An exception occurred whilst encrypting data:", e);
                    }
                }

                if (!peek.getValue().test(wrappedBuffer)) {
                    queue.pollLast();
                }

                if (wrappedBuffer.hasRemaining()) {
                    int remaining = wrappedBuffer.remaining();
                    byte[] decodedData = new byte[Math.min(key, 8)];
                    //wrappedBuffer.reset().get(decodedData);
                    System.out.println("A packet has not been read fully! " + remaining + " byte(s) leftover! First 8 bytes of data: " + Arrays.toString(decodedData) + "!");
                }

                buffer.position(wrappedBuffer.limit());

                while (!stack.isEmpty()) {
                    queue.offerLast(stack.pop());
                }

                if ((peek = queue.peekLast()) == null) {
                    queueIsEmpty = true;
                    break;
                }
            }

            lightningClient.getInCallback().set(false);

            if (!queueIsEmpty && buffer.hasRemaining()) {
                lightningClient.getChannel().read((ByteBuffer) buffer.position(buffer.limit()).limit(key), pair, this);
            } else {
                LightningClient.DIRECT_BUFFER_POOL.give(buffer);

                if (queueIsEmpty) {
                    lightningClient.getReadInProgress().set(false);
                } else {
                    ByteBuffer newBuffer = LightningClient.DIRECT_BUFFER_POOL.take(peek.getKey());
                    lightningClient.getChannel().read(newBuffer, new Pair<>(lightningClient, newBuffer), this);
                }
            }
        }
    }

    @Override
    public void failed(Throwable t, Pair<LightningClient, ByteBuffer> pair) {
        pair.getKey().close(false);
    }
}
