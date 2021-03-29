package io.vera.server.packet.handshake;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.login.LoginOutDisconnect;
import io.vera.ui.chat.ChatComponent;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class HandshakeIn extends PacketIn {

    public HandshakeIn() {
        super(HandshakeIn.class);
    }

    public void read(ByteBuf buf, NetClient client) {
        int version = NetData.rvint(buf);
        String address = NetData.rstr(buf);
        int port = buf.readUnsignedShort();
        int nextState = NetData.rvint(buf);
        if (version != 47)
            client.sendPacket(new LoginOutDisconnect(ChatComponent.text("This VeraServer runs on Version 1.8.9 so you can't join with " + version)));
        client.setState(NetClient.NetState.values()[nextState]);
    }
}
