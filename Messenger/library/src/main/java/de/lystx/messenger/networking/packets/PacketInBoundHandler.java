package de.lystx.messenger.networking.packets;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.networking.connection.packet.Packet;
import de.lystx.messenger.networking.netty.NettyConnection;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public abstract class PacketInBoundHandler<R> extends Packet implements Serializable {

    protected UUID uniqueId;
    protected Result<R> result;

    public abstract R handleRead(MessageAPI messageAPI);

    public void execptionCaught(NettyConnection connection, Throwable cause) {}

    public PacketInBoundHandler<R> uuid(UUID uuid) {
        this.setUniqueId(uuid);
        return this;
    }
}
