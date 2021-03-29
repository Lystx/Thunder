package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.chats.ChatMessage;
import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutSendMessage extends Packet implements Serializable {

    private final Chat chat;
    private final ChatMessage chatMessage;
}
