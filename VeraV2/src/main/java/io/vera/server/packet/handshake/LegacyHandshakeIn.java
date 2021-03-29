package io.vera.server.packet.handshake;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LegacyHandshakeIn extends PacketIn {

    public LegacyHandshakeIn() {
        super(LegacyHandshakeIn.class);
    }

    public void read(ByteBuf buf, NetClient client) {
        if (buf.readUnsignedByte() != 1)
            throw new RuntimeException("Legacy handshake has the wrong schema");
    }
}
