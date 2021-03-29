package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketOutClientConnected extends Packet implements Serializable {


    private final String socketAddress;

}
