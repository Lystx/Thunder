package de.lystx.messenger.networking.netty;

import de.lystx.messenger.networking.connection.adapter.PacketAdapter;
import de.lystx.messenger.networking.connection.packet.Packet;
import de.lystx.messenger.networking.connection.packet.PacketState;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class NettyConnection extends Thread {

    protected Channel channel;
    protected boolean running;

    protected final String host;
    protected final int port;
    protected final PacketAdapter packetAdapter;
    protected final List<Channel> registeredChannels;

    public NettyConnection(String host, int port) {
        this.packetAdapter = new PacketAdapter();
        this.running = true;
        this.host = host;
        this.port = port;
        this.channel = null;
        this.registeredChannels = new LinkedList<>();
    }

    /**
     * Abstract Method to send a {@link Packet}
     * @param packet
     */
    public abstract void sendPacket(Packet packet);

    /**
     * Abstract Method to send a {@link Packet}
     * @param packet
     * @param consumer
     */
    public abstract void sendPacket(Packet packet, Consumer<PacketState> consumer);

    /**
     * Closes NettyConnection
     */
    public void disconnect() {
        this.interrupt();
        this.channel.close();
    }

    /**
     * Builds the {@link ChannelFutureListener}
     * for both; Server and Client
     * @param consumer
     * @return
     */
    public ChannelFutureListener channelFutureListener(Consumer<PacketState> consumer) {
        return channelFuture -> {
            if (consumer == null) {
                return;
            }
            if (channelFuture.isSuccess()) {
                consumer.accept(PacketState.SUCCESS);
            } else {
                consumer.accept(PacketState.FAILED);
            }
        };
    }

    /**
     * Builds the {@link ChannelInitializer}
     * for the ConnectionServer
     * @return
     */
    public ChannelInitializer<?> clientInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {

                ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                channelPipeline.addLast(new LengthFieldPrepender(2));
                channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(NettyConnection.class.getClassLoader())));
                channelPipeline.addLast(new ObjectEncoder());
                channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                    }

                    @Override
                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                        packetAdapter.handelAdapterHandler(packet);
                    }
                });
                channel = socketChannel;
            }
        };
    }

}
