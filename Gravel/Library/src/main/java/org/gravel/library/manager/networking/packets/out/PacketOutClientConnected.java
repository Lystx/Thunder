package org.gravel.library.manager.networking.packets.out;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketOutClientConnected extends ThunderPacket implements Serializable {

    private final String socketAddress;

    @Override
    public int getPacketID() {
        return 0x03;
    }
}
