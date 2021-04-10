

package io.lightning.network.packet;

import io.lightning.network.connection.LightningBase;
import io.lightning.network.connection.client.LightningClient;
import io.lightning.network.utility.LightningUtils;
import lombok.Getter;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Queue;
import java.util.function.Consumer;

@Getter
public class Packet {

    private boolean prepend;
    private int size;
    private final Deque<Consumer<ByteBuffer>> stack;
    private final Deque<Consumer<ByteBuffer>> queue;

    public Packet() {
        this.queue = new ArrayDeque<>(4);
        this.stack = new ArrayDeque<>(1);
    }

    private Packet enqueue(Consumer<ByteBuffer> consumer) {
        if (prepend) {
            stack.push(consumer);
        } else {
            queue.offerLast(consumer);
        }

        return this;
    }

    public Packet put(int b) {
        size += Byte.BYTES;
        return enqueue(buffer -> buffer.put((byte) b));
    }

    public void set(Object o) {
        if (o instanceof Boolean) {

            Boolean b = (Boolean) o;
            size += Byte.BYTES;
            this.enqueue(buffer -> buffer.put(b ? (byte) 1 : 0));

        } else if (o instanceof String) {

            String s = (String) o;
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            size += Short.BYTES;
            short value = (short) bytes.length;
            this.enqueue(buffer -> buffer.putShort(ByteOrder.BIG_ENDIAN == ByteOrder.LITTLE_ENDIAN ? Short.reverseBytes(value) : value));
            size += Byte.BYTES * bytes.length;
            this.enqueue(buffer -> buffer.put(bytes));

        } else if (o instanceof Character) {

            char c = (char) o;
            size += Character.BYTES;
            this.enqueue(buffer -> buffer.putChar(c));

        } else if (o instanceof Long) {

            long l = (long) o;
            size += Long.BYTES;
            this.enqueue(buffer -> buffer.putLong(l));

        } else if (o instanceof Double) {

            Double d = (Double) o;
            this.set(Double.doubleToRawLongBits(d));

        } else if (o instanceof Integer) {

            Integer i = (Integer) o;
            size += Integer.BYTES;
            this.enqueue(buffer -> buffer.putInt(i));

        } else if (o instanceof Float) {

            Float f = (Float) o;
            this.set(Float.floatToRawIntBits(f));

        } else {
            throw new UnsupportedOperationException("Can't set Object from class " + o.getClass().getSimpleName() + "!");
        }
    }


    public final void queue(LightningClient lightningClient) {
        Queue<Packet> clientQueue;
        
        synchronized ((clientQueue = lightningClient.getOutgoingPackets())) {
            clientQueue.offer(this);
        }
    }

    public final void queue(LightningClient... lightningClients) {
        for (LightningClient lightningClient : lightningClients) {
            queue(lightningClient);
        }
    }

    public final void queueAndFlush(LightningClient lightningClient) {
        queue(lightningClient);
        lightningClient.flush();
    }

    public final void queueAndFlush(LightningClient... lightningClients) {
        for (LightningClient lightningClient : lightningClients) {
            queueAndFlush(lightningClient);
        }
    }

    public final void queueAndFlush(Collection<? extends LightningClient> clients) {
        clients.forEach(this::queueAndFlush);
    }

    public int getSize(LightningBase<?> base) {
        Cipher encryptionCipher;
        
        if (base == null || (encryptionCipher = base.getEncryptionCipher()) == null) {
            return size;
        }

        if (!base.isEncryptionNoPadding()) {
            int blockSize = encryptionCipher.getBlockSize();
            return LightningUtils.roundUpToNextMultiple(size, blockSize == 0 ?
                encryptionCipher.getOutputSize(size) : blockSize);
        }
        return size;
    }

    public Deque<Consumer<ByteBuffer>> getQueue() {
        return queue;
    }
}
