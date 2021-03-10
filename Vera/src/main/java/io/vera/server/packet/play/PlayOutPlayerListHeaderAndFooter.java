
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutPlayerListHeaderAndFooter extends PacketOut {

    private final ChatComponent header;
    private final ChatComponent footer;

    public PlayOutPlayerListHeaderAndFooter(ChatComponent header, ChatComponent footer) {
        super(PlayOutPlayerListHeaderAndFooter.class);
        this.header = header;
        this.footer = footer;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wstr(buf, this.header == null ? ChatComponent.empty().toString() : this.header.toString());
        NetData.wstr(buf, this.header == null ? ChatComponent.empty().toString() : this.footer.toString());
    }

}