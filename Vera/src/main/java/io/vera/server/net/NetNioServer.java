
package io.vera.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.vera.server.concurrent.PoolSpec;

import javax.annotation.concurrent.Immutable;


@Immutable
public class NetNioServer extends NetServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);

    public NetNioServer(String ip, int port) {
        super(ip, port);
    }

    @Override
    public void setup() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NetChannelInit())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        b.bind(this.ip(), this.port());
    }

    @Override
    public void shutdown() throws InterruptedException {
        this.bossGroup.shutdownGracefully().await();
        this.workerGroup.shutdownGracefully().await();
    }
}