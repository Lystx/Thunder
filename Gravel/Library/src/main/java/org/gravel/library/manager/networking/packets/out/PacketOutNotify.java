package org.gravel.library.manager.networking.packets.out;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutNotify extends ThunderPacket implements Serializable {

    private final GravelUser gravelUser;
    private final String message;
    private final String title;
    private final int id;

    @Override
    public int getPacketID() {
        return 0x04;
    }
}
