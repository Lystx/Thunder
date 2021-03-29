package io.vera.server.packet.status;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class StatusInPing extends PacketIn {
    public StatusInPing() {
        super(StatusInPing.class);
    }

    public void read(ByteBuf buf, NetClient client) {
        long endStamp = System.currentTimeMillis();
        long time = buf.readLong();
        client.getPing().set(endStamp - time);
        client.sendPacket(new StatusOutPong(time));
    }
}
