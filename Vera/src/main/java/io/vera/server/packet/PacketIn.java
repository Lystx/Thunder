
package io.vera.server.packet;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;

import javax.annotation.concurrent.Immutable;


@Immutable
public abstract class PacketIn extends Packet {

    public PacketIn(Class<? extends Packet> cls) {
        super(cls);
    }

    public abstract void read(ByteBuf buf, NetClient client);
}