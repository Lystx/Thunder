package io.vera.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.vera.server.concurrent.PoolSpec;
import javax.annotation.concurrent.Immutable;

@Immutable
public class NetNioServer extends NetServer {
  private final EventLoopGroup bossGroup = (EventLoopGroup)new NioEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);
  
  private final EventLoopGroup workerGroup = (EventLoopGroup)new NioEventLoopGroup(0, PoolSpec.UNCAUGHT_FACTORY);
  
  public NetNioServer(String ip, int port) {
    super(ip, port);
  }
  
  public void setup() {
    ServerBootstrap b = new ServerBootstrap();
    ((ServerBootstrap)((ServerBootstrap)b.group(this.bossGroup, this.workerGroup)
      .channel(NioServerSocketChannel.class))
      .childHandler((ChannelHandler)new NetChannelInit())
      .option(ChannelOption.SO_BACKLOG, Integer.valueOf(128)))
      .childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
    b.bind(ip(), port());
  }
  
  public void shutdown() throws InterruptedException {
    this.bossGroup.shutdownGracefully().await();
    this.workerGroup.shutdownGracefully().await();
  }
}
