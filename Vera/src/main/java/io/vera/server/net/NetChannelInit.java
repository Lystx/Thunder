
package io.vera.server.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import javax.annotation.concurrent.Immutable;


@Immutable
public class NetChannelInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipe = socketChannel.pipeline();

        pipe.addLast(new InDecoder());
        pipe.addLast(new OutEncoder());
    }
}