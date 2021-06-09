package io.thunder.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This Packet is used to verify some connections
 * between {@link io.thunder.connection.base.ThunderServer} and {@link io.thunder.connection.base.ThunderClient}
 * for at least a bit of authentication
 */
@Getter @AllArgsConstructor
public class PacketHandshake extends EmptyPacket {

    private final String name;


    public PacketHandshake() {
        this("<none>");
    }
}
