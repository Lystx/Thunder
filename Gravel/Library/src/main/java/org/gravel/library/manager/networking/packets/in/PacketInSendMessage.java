package org.gravel.library.manager.networking.packets.in;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.chatting.ChatMessage;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketInSendMessage extends ThunderPacket implements Serializable {

    private final UUID chatUUID;
    private final ChatMessage chatMessage;

    @Override
    public int getPacketID() {
        return 0x02;
    }
}
