package org.gravel.library.manager.networking.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.connection.packet.Packet;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketInSendMessage extends Packet implements Serializable {

    private final UUID chatUUID;
    private final ChatMessage chatMessage;
}
