package net.hytora.library.manager.networking.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.hytora.library.manager.networking.connection.packet.Packet;
import net.hytora.library.manager.networking.connection.packet.PacketState;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class ConnectionServer extends NettyConnection {

    public ConnectionServer(String host, int port) {
        super(host, port);
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(workerGroup, bossGroup);
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(2147483647, 0, 2, 0, 2));
                channelPipeline.addLast(new LengthFieldPrepender(2));
                channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(getClass().getClassLoader())));
                channelPipeline.addLast(new ObjectEncoder());
                channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {

                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                        packetAdapter.handelAdapterHandler(packet);
                    }

                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        if (cause instanceof java.io.IOException) {
                            return;
                        }
                        cause.printStackTrace();
                    }
                });
                InetSocketAddress inetSocketAddress = socketChannel.remoteAddress();
                registeredChannels.add(socketChannel);
            }
        });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(this.host, this.port).sync();
            this.channel = channelFuture.channel();
            this.running = true;
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
            this.running = false;
        } finally {
            this.running = false;
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void sendPacket(Packet packet) {
        sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {
        for (Channel registeredChannel : getRegisteredChannels())
            registeredChannel.writeAndFlush(packet).addListener(this.channelFutureListener(consumer));
    }
}
