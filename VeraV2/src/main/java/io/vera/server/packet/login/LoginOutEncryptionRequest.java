package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class LoginOutEncryptionRequest extends PacketOut {

    private final byte[] publicKey;
    private final byte[] token;

    public LoginOutEncryptionRequest(byte[] publicKey, byte[] token) {
        super(LoginOutEncryptionRequest.class);
        this.publicKey = publicKey;
        this.token = token;
    }

    public void write(ByteBuf buf) {
        NetData.wvint(buf, 0);
        NetData.wvint(buf, this.publicKey.length);
        buf.writeBytes(this.publicKey);
        NetData.wvint(buf, this.token.length);
        buf.writeBytes(this.token);
    }
}
