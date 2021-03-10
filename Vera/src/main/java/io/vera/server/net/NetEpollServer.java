
package io.vera.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.vera.server.concurrent.PoolSpec;

import javax.annotation.concurrent.Immutable;


@Immutable
public class NetEpollServer extends NetServer {
    private final EventLoopGroup bossGroup = new EpollEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);
    private final EventLoopGroup workerGroup = new EpollEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);

    NetEpollServer(String ip, int port) {
        super(ip, port);
    }

    @Override
    public void setup() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(this.bossGroup, this.workerGroup)
                .channel(EpollServerSocketChannel.class)
                .childHandler(new NetChannelInit())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        b.bind(this.ip(), this.port());
    }

    @Override
    public void shutdown() throws InterruptedException {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}