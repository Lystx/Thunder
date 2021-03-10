
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;

/**
 * Disconnect packet for PLAY.
 */
@Immutable
public final class PlayOutDisconnect extends PacketOut {
    /**
     * The reason why the player is disconnected
     */
    private final ChatComponent reason;

    public PlayOutDisconnect(ChatComponent reason) {
        super(PlayOutDisconnect.class);
        this.reason = reason;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wstr(buf, this.reason.toString());
    }
}
