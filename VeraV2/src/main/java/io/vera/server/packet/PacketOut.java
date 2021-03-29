package io.vera.server.packet;

import io.netty.buffer.ByteBuf;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class PacketOut extends Packet {
    public PacketOut(Class<? extends Packet> cls) {
        super(cls);
    }

    public abstract void write(ByteBuf paramByteBuf);
}
