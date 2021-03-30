package org.gravel.library.manager.networking.packets;

import lombok.Getter;
import lombok.Setter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.networking.netty.NettyConnection;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public abstract class PacketInBoundHandler<R> extends Packet implements Serializable {

    protected UUID uniqueId;
    protected Result<R> result;

    public void execptionCaught(NettyConnection connection, Throwable cause) {}

    public PacketInBoundHandler<R> uuid(UUID uuid) {
        this.uniqueId = uuid;
        return this;
    }

    public abstract R handleRead(GravelAPI gravelAPI);
}
