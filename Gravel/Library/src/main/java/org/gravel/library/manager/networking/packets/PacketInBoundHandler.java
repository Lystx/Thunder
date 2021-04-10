package org.gravel.library.manager.networking.packets;

import io.thunder.manager.packet.ThunderPacket;
import lombok.Getter;
import lombok.Setter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.netty.GravelConnection;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public abstract class PacketInBoundHandler<R> extends ThunderPacket implements Serializable {

    protected UUID uuid;
    protected Result<R> result;

    public void execptionCaught(GravelConnection connection, Throwable cause) {}

    public PacketInBoundHandler<R> uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public int getPacketID() {
        return -1;
    }

    public abstract R handleRead(GravelAPI gravelAPI);
}
