
package io.lightning.network.connection.client;

import io.lightning.network.Channeled;
import io.lightning.network.connection.LightningBase;
import io.lightning.network.bufferpools.AbstractBufferPool;
import io.lightning.network.bufferpools.direct.DirectByteBufferPool;
import io.lightning.network.packet.Packet;
import io.lightning.network.utility.pair.IntegerPair;
import io.lightning.network.utility.other.MutableBoolean;
import io.lightning.network.utility.pair.Pair;
import io.lightning.network.utility.exposed.cryptography.CryptographicFunction;
import io.lightning.network.utility.exposed.data.BooleanReader;
import io.lightning.network.utility.exposed.data.ByteReader;
import io.lightning.network.utility.exposed.data.CharReader;
import io.lightning.network.utility.exposed.data.DoubleReader;
import io.lightning.network.utility.exposed.data.FloatReader;
import io.lightning.network.utility.exposed.data.IntReader;
import io.lightning.network.utility.exposed.data.LongReader;
import io.lightning.network.utility.exposed.data.StringReader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.Getter;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class LightningClient extends LightningBase<Consumer<LightningClient>> implements Channeled<AsynchronousSocketChannel>, BooleanReader, ByteReader, CharReader, IntReader, FloatReader, LongReader, DoubleReader, StringReader {

    public static final AbstractBufferPool<ByteBuffer> DIRECT_BUFFER_POOL = new DirectByteBufferPool();
    private final MutableBoolean inCallback;
    private final AtomicBoolean closing;
    private final AtomicBoolean readInProgress;
    private final AtomicBoolean writeInProgress;
    private final Queue<Packet> outgoingPackets;
    private final Queue<ByteBuffer> packetsToFlush;
    private final Deque<IntegerPair<Predicate<ByteBuffer>>> stack;
    private final Deque<IntegerPair<Predicate<ByteBuffer>>> queue;
    private AsynchronousChannelGroup group;
    private AsynchronousSocketChannel channel;

    public LightningClient() {
        this(null);
    }

    @Override
    public LightningBase<?> build(String host, int port, int threads) {
        throw new UnsupportedOperationException("Not supported on LightningClient");
    }

    public LightningClient(AsynchronousSocketChannel channel) {
        this.closing = new AtomicBoolean();
        this.inCallback = new MutableBoolean();
        this.readInProgress = new AtomicBoolean();
        this.writeInProgress = new AtomicBoolean();
        this.outgoingPackets = new ArrayDeque<>();
        this.packetsToFlush = new ArrayDeque<>();
        this.queue = new ArrayDeque<>();
        this.stack = new ArrayDeque<>();
        
        if (channel != null) {
            this.channel = channel;
        }


        this.onConnect(client -> {
            new Thread(() -> {
                while (client.getChannel() != null) {
                    final ByteBuffer readTo = ByteBuffer.allocate(1024);
                    if (client.getChannel() == null) {
                        continue;
                    }
                    try {
                        client.getChannel()
                                .read(
                                        readTo,
                                        readTo,
                                        new CompletionHandler<Integer, ByteBuffer>() {
                                            public void completed(Integer bytesRead, ByteBuffer buffer) {
                                                buffer.flip();
                                                System.out.println(StandardCharsets.US_ASCII.decode(buffer).toString());
                                                client.close();
                                            }

                                            public void failed(Throwable exc, ByteBuffer attachment) {
                                                client.close();
                                            }
                                        });

                    } catch (ReadPendingException e) {
                    }
                }
            }).start();
        });
    }


    public LightningClient build(String address, int port) {
        return this.build(address, port, 30L, TimeUnit.SECONDS, () -> System.out.println("[Lightning] Couldn't connect to the server! Maybe it's offline?"));
    }

    public LightningClient build(String address, int port, long timeout, TimeUnit unit, Runnable onTimeout) {
        Objects.requireNonNull(address);

        if (port < 0 || port > 65_535) {
            throw new IllegalArgumentException("The specified port must be between 0 and 65535!");
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(false);
            thread.setName(thread.getName().replace("Thread", "Lightning"));
            
            return thread;
        }, (runnable, threadPoolExecutor) -> {});

        executor.prestartCoreThread();

        try {
            this.channel = AsynchronousSocketChannel.open(group = AsynchronousChannelGroup.withThreadPool(executor));
            this.channel.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE);
            this.channel.setOption(StandardSocketOptions.SO_SNDBUF, BUFFER_SIZE);
            this.channel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
            this.channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open the channel!", e);
        }

        try {
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);
            channel.connect(inetSocketAddress).get(timeout, unit);
        } catch (AlreadyConnectedException e) {
            throw new IllegalStateException("This client is already connected to a server!", e);
        } catch (Exception e) {
            onTimeout.run();
            close(false);
            return this;
        }

        executor.execute(() -> connectListeners.forEach(lightningClientConsumer -> lightningClientConsumer.accept(this)));
        return this;
    }



    public void close(boolean waitForWrite) {
        if (closing.getAndSet(true)) {
            return;
        }

        preDisconnectListeners.forEach(lightningClientConsumer -> lightningClientConsumer.accept(this));

        if (waitForWrite) {
            flush();

            while (writeInProgress.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Channeled.super.close();

        while (channel.isOpen()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        postDisconnectListeners.forEach(lightningClientConsumer -> lightningClientConsumer.accept(this));

        if (group != null) {
            try {
                group.shutdownNow();
            } catch (IOException e) {
                System.out.println("An IOException occurred when shutting down the AsynchronousChannelGroup!");
            }
        }
    }

    @Override
    public final void close() {
        close(true);
    }

    public final void postDisconnect(Consumer<LightningClient> listener) {
        postDisconnectListeners.add(listener);
    }
    
    @Override
    public void readUntil(int n, Predicate<ByteBuffer> predicate, ByteOrder order) {

        IntegerPair<Predicate<ByteBuffer>> pair = new IntegerPair<>(n, buffer -> predicate.test(buffer.order(order)));

        synchronized (queue) {
            if (inCallback.get()) {
                stack.push(pair);
                return;
            }

            queue.offerFirst(pair);

            if (!readInProgress.getAndSet(true)) {
                ByteBuffer buffer = DIRECT_BUFFER_POOL.take(n);
                channel.read(buffer, new Pair<>(this, buffer), Listener.INSTANCE);

            }
        }
    }

    public final void flush() {
        Packet packet;

        boolean shouldEncrypt = encryptionCipher != null;

        Deque<Consumer<ByteBuffer>> queue;

        synchronized (outgoingPackets) {
            while ((packet = outgoingPackets.poll()) != null) {
                queue = packet.getQueue();

                ByteBuffer raw = DIRECT_BUFFER_POOL.take(packet.getSize(this));

                for (Consumer<ByteBuffer> input : queue) {
                    input.accept(raw);
                }

                if (shouldEncrypt) {
                    try {
                        raw = encryptionFunction.apply(encryptionCipher, (ByteBuffer) raw.flip());
                    } catch (GeneralSecurityException e) {
                        throw new IllegalStateException("An exception occurred whilst encrypting data!", e);
                    }
                }

                raw.flip();

                if (!writeInProgress.getAndSet(true)) {
                    if (channel == null) {
                        return;
                    }
                    channel.write(raw, raw, new ClientPacketHandler(this));
                } else {
                    packetsToFlush.offer(raw);
                }
            }
        }
    }



    public LightningClient queue(Packet packet) {

        VsonObject encode = VsonObject.encode(new HashMap<>());
        encode.append("_packet", packet);

        packet.put(0).set(packet.getClass().getSimpleName());
        packet.put(1).set(encode.toString(FileFormat.RAW_JSON));

        Queue<Packet> clientQueue;

        synchronized ((clientQueue = getOutgoingPackets())) {
            clientQueue.offer(packet);
        }
        return this;
    }

}
