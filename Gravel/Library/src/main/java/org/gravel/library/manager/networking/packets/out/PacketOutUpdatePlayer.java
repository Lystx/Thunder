package org.gravel.library.manager.networking.packets.out;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.user.GravelUser;

@Getter @AllArgsConstructor
public class PacketOutUpdatePlayer extends ThunderPacket {

    private final GravelUser gravelUser;

    @Override
    public int getPacketID() {
        return 0x06;
    }
}
