
package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.VeraServer;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetCrypto;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class LoginInStart extends PacketIn {

    public LoginInStart() {
        super(LoginInStart.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        String name = NetData.rstr(buf);
        client.setName(name);

        if (!Login.canLogin(client)) {
            return;
        }

        if (name.length() > 16 || !name.matches("[a-zA-Z0-9_]+")) {
            client.disconnect("Invalid name");
            return;
        }

        if (VeraServer.cfg().doAuth()) {
            NetCrypto crypto = client.initCrypto();
            client.sendPacket(crypto.reqCrypto());
        } else {
            LoginOutSuccess packet = new LoginOutSuccess(client);
            client.sendPacket(packet).addListener(future ->
                    VeraPlayer.spawn(client, name, packet.getUuid(), packet.getTextures()));
        }
    }
}