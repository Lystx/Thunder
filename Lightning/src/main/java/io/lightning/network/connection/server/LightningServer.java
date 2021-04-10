
package io.lightning.network.connection.server;

import io.lightning.network.Channeled;
import io.lightning.network.connection.LightningBase;
import io.lightning.network.connection.client.ClientPacketHandler;
import io.lightning.network.connection.client.LightningClient;
import io.lightning.network.packet.Packet;
import io.lightning.network.utility.other.LightningSettings;
import io.lightning.network.utility.exposed.consumer.ByteConsumer;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Getter
public class LightningServer extends LightningBase<Consumer<LightningClient>> implements Channeled<AsynchronousServerSocketChannel> {

    private final Set<LightningClient> connectedClients;
    private final List<Packet> queue;

    private AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel channel;

    public LightningServer() {
        this.connectedClients = new HashSet<>();
        this.queue = new LinkedList<>();
    }

    public LightningServer build(String address, int port) {
        return this.build(address, port, Math.max(2, Runtime.getRuntime().availableProcessors() - 2));
    }

    @Override
    public LightningServer queue(Packet packet) {
        this.queue.add(packet);
        return this;
    }

    @Override
    public void flush() {
        for (Packet packet : this.queue) {
            this.queue.remove(packet);

            ByteBuffer raw = LightningClient.DIRECT_BUFFER_POOL.take(packet.getSize(this));


            for (LightningClient connectedClient : this.connectedClients) {
                //TODO: SEND PACKETS FROM SERVER TO CLIENT
            }

        }
    }


    public LightningServer build(String address, int port, int numThreads) {
        Objects.requireNonNull(address);

        if (port < 0 || port > 65535) {
            if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                throw new IllegalArgumentException("The port must be between 0 and 65535!");
            }
            return this;
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(false);
            thread.setName(thread.getName().replace("Thread", "Lightning"));
            return thread;
        }, (runnable, threadPoolExecutor) -> {});

        executor.prestartCoreThread();

        try {
            this.channel = AsynchronousServerSocketChannel.open(group = AsynchronousChannelGroup.withThreadPool(executor));
            this.channel.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE);
        } catch (IOException e) {
            if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                throw new IllegalStateException("Unable to open the AsynchronousServerSocketChannel!", e);
            }
        }

        try {
            channel.bind(new InetSocketAddress(address, port));
            channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel channel, Void attachment) {
                    LightningClient lightningClient = new LightningClient(channel);
                    connectedClients.add(lightningClient);
                    connectionThread(lightningClient).start();
                    lightningClient.postDisconnect(connectedClients::remove);
                    connectListeners.forEach(consumer -> consumer.accept(lightningClient));
                    LightningServer.this.channel.accept(null, this);


                }

                @Override
                public void failed(Throwable t, Void attachment) {
                    if (settings.contains(LightningSettings.LOG_ENABLED)) {
                        System.out.println("An exception occurred when accepting a Client!");
                    }
                    if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                        t.printStackTrace();
                    }
                }
            });

            if (this.settings.contains(LightningSettings.LOG_ENABLED)) {
                System.out.println("[LightningServer] Successfully bound to " + address + ":" + port + "!");
            }
        } catch (AlreadyBoundException e) {
            if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                throw new IllegalStateException("This server is already bound!", e);
            }
        } catch (IOException e) {
            if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                throw new IllegalStateException("Unable to bind the specified address and port!", e);
            }
        }
        return this;
    }


    public Thread connectionThread(LightningClient client) {
        return new Thread(() -> {
            while (client.getChannel() != null) {
                AtomicReference<String> key = new AtomicReference<>();

                client.handleIncomingBytes(value -> {

                    switch (value) {
                        case 0:
                            client.readString(key::set);
                            break;
                        case 1:
                            client.readString(s -> {
                                try {
                                    VsonObject vsonObject = new VsonObject(s);
                                    Packet simplePacket = (Packet) vsonObject.getObject("_packet", Class.forName(key.get()));
                                    packetAdapter.handle(simplePacket);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        default:
                            break;
                    }
                });
            }
        });
    }

    @Override
    public void close() {
        this.connectedClients.removeIf(client -> {
            client.close();
            return true;
        });

        Channeled.super.close();

        try {
            group.shutdownNow();
        } catch (IOException e) {
            if (settings.contains(LightningSettings.LOG_ENABLED)) {
                System.out.println("An IOException occurred when shutting down the AsynchronousChannelGroup!");
            }
            if (settings.contains(LightningSettings.FIRE_EXCEPTIONS)) {
                e.printStackTrace();
            }
        }
    }

    private void queueHelper(Consumer<LightningClient> consumer, LightningClient... lightningClients) {
        Set<LightningClient> toExclude = Collections.newSetFromMap(new IdentityHashMap<>(lightningClients.length));
        Collections.addAll(toExclude, lightningClients);
        connectedClients.stream().filter(client -> !toExclude.contains(client)).forEach(consumer);
    }

    private void queueHelper(Consumer<LightningClient> consumer, Collection<? extends LightningClient> clients) {
        Set<LightningClient> toExclude = Collections.newSetFromMap(new IdentityHashMap<>(clients.size()));
        toExclude.addAll(clients);
        connectedClients.stream().filter(client -> !toExclude.contains(client)).forEach(consumer);
    }

    public final void queueToAllExcept(Packet packet, LightningClient... lightningClients) {
        queueHelper(packet::queue, lightningClients);
    }

    public final void queueToAllExcept(Packet packet, Collection<? extends LightningClient> clients) {
        queueHelper(packet::queue, clients);
    }

    public final void flushToAllExcept(LightningClient... lightningClients) {
        queueHelper(LightningClient::flush, lightningClients);
    }

    public final void flushToAllExcept(Collection<? extends LightningClient> clients) {
        queueHelper(LightningClient::flush, clients);
    }

    public final void queueAndFlushToAllExcept(Packet packet, LightningClient... lightningClients) {
        queueHelper(packet::queueAndFlush, lightningClients);
    }

    public final void queueAndFlushToAllExcept(Packet packet, Collection<? extends LightningClient> clients) {
        queueHelper(packet::queueAndFlush, clients);
    }
}
