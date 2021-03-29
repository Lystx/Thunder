package de.lystx.messenger.networking.packets.in;

import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCreateAccount extends Packet implements Serializable {

    private final String name;
    private final String password;
    private final String passwordRe;
    private final String ip;

}
