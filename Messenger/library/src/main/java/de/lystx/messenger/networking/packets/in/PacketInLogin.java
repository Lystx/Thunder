package de.lystx.messenger.networking.packets.in;

import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInLogin extends Packet implements Serializable {

    private final String name, password;
}
