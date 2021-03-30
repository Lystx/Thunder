package org.gravel.library.manager.networking.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;
import org.gravel.library.manager.networking.connection.adapter.PacketAdapter;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.networking.connection.packet.PacketState;

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

    public void disconnect() {
        interrupt();
        this.channel.close();
    }

    public abstract void sendPacket(Packet paramPacket);

    public abstract void sendPacket(Packet paramPacket, Consumer<PacketState> paramConsumer);

    public ChannelFutureListener channelFutureListener(Consumer<PacketState> consumer) {
        return channelFuture -> {
            if (consumer == null)
                return;
            if (channelFuture.isSuccess()) {
                consumer.accept(PacketState.SUCCESS);
            } else {
                consumer.accept(PacketState.FAILED);
            }
        };
    }

    public ChannelInitializer<?> clientInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(2147483647, 0, 2, 0, 2));
                channelPipeline.addLast(new LengthFieldPrepender(2));
                channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(NettyConnection.class.getClassLoader())));
                channelPipeline.addLast(new ObjectEncoder());
                channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {

                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                    }

                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                        packetAdapter.handelAdapterHandler(packet);
                    }
                });
                channel = socketChannel;
            }
        };
    }
}
