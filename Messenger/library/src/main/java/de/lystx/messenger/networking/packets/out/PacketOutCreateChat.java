package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketOutCreateChat extends Packet implements Serializable {


    private final Chat chat;

}
