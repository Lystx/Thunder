package net.hytora.library.manager.networking.packets;

import lombok.Getter;
import lombok.Setter;
import net.hytora.library.CloudAPI;
import net.hytora.library.manager.networking.connection.packet.Packet;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public abstract class QueryPacket<R> extends Packet implements Serializable {

    protected UUID uniqueId;
    protected Result<R> result;

    public QueryPacket<R> uuid(UUID uuid) {
        this.uniqueId = uuid;
        return this;
    }

    public abstract R read(CloudAPI cloudAPI);
}
