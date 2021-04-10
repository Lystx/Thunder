package org.gravel.library.manager.networking.packets.out;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatMessage;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketOutSendMessage extends ThunderPacket implements Serializable {

    private final Chat chat;
    private final ChatMessage chatMessage;

    @Override
    public int getPacketID() {
        return 0x05;
    }
}
