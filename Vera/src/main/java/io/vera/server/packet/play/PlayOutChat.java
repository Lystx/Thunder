
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.ui.chat.ChatComponent;
import io.vera.ui.chat.ChatType;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

/**
 * This packet is sent by the server to broadcast an
 * incoming chat packet to the entire server, or to send
 * a message to certain players.
 */
@Immutable
@Getter
public final class PlayOutChat extends PacketOut {
    /**
     * The chat message being sent
     */
    private final ChatComponent chat;
    /**
     * The type of chat being sent to the player
     */
    private final ChatType type;
    /**
     * Whether or not chat colors is enabled
     */
    private final boolean chatColors;

    public PlayOutChat(ChatComponent chat, ChatType type, boolean chatColors) {
        super(PlayOutChat.class);
        this.chat = chat;
        this.type = type;
        this.chatColors = chatColors;
    }

    @Override
    public void write(ByteBuf buf) {
        if (!this.chatColors) {
            NetData.wstr(buf, this.chat.stripColor().toString());
        } else {
            NetData.wstr(buf, this.chat.toString());
        }

        buf.writeByte(this.type.ordinal());
    }
}