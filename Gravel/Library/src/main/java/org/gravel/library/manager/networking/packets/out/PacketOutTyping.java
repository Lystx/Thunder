package org.gravel.library.manager.networking.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutTyping extends Packet implements Serializable {

    private final GravelUser gravelUser;
}
