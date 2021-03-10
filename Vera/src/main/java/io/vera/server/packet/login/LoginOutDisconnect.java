
package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.ui.chat.ChatComponent;

public final class LoginOutDisconnect extends PacketOut {

    private final ChatComponent reason;

    public LoginOutDisconnect(ChatComponent reason) {
        super(LoginOutDisconnect.class);
        this.reason = reason;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wstr(buf, this.reason.toString());
    }
}
