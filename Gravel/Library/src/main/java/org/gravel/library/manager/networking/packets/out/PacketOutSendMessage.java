package org.gravel.library.manager.networking.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.connection.packet.Packet;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketOutSendMessage extends Packet implements Serializable {

    private final Chat chat;
    private final ChatMessage chatMessage;
}
