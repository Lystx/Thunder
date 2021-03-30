package org.gravel.library.manager.networking.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.networking.connection.packet.PacketState;

import java.util.function.Consumer;

public class ConnectionClient extends NettyConnection {

    private Consumer<Exception> consumer;

    public ConnectionClient(String host, int port) {
        super(host, port);
    }

    public void run() {
        try {
            Bootstrap bootstrap;
            EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            try {
                bootstrap = (new Bootstrap())
                        .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                        .group(workerGroup)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.IP_TOS, 24)
                        .option(ChannelOption.SO_KEEPALIVE, true).handler(this.clientInitializer());
            } catch (IllegalAccessError e) {
                bootstrap = null;
            }
            if (bootstrap == null) {
                System.out.println("[Client] Couldn't build up Bootstrap for Client!");
                return;
            }
            try {
                ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
                this.channel = channelFuture.channel();
                this.running = true;
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.running = false;
            } finally {
                this.running = false;
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            if (this.consumer != null)
                this.consumer.accept(e);
        }
    }

    public void onError(Consumer<Exception> consumer) {
        this.consumer = consumer;
    }

    public void sendPacket(Packet packet) {
        sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {
        if (this.channel != null)
            if (this.channel.eventLoop().inEventLoop()) {
                this.channel.writeAndFlush(packet).addListener(this.channelFutureListener(consumer)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                try {
                    this.channel.eventLoop().execute(() -> this.channel.writeAndFlush(packet).addListener(this.channelFutureListener(consumer)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE));
                } catch (NullPointerException ignored) {
                    consumer.accept(PacketState.FAILED);
                }
            }
    }
}
