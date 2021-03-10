
package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.VeraServer;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class LoginOutCompression extends PacketOut {

    public LoginOutCompression() {
        super(LoginOutCompression.class);
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, VeraServer.cfg().compressionThresh());
    }
}
