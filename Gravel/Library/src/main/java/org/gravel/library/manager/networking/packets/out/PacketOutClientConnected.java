package org.gravel.library.manager.networking.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.networking.connection.packet.Packet;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketOutClientConnected extends Packet implements Serializable {

    private final String socketAddress;

}
