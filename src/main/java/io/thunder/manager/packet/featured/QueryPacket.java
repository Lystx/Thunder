package io.thunder.manager.packet.featured;

import io.thunder.manager.packet.BufferedPacket;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.PacketReader;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This packet is used to get Realtime-Results
 * from the other site of your connection
 */
@Setter @Getter
public abstract class QueryPacket extends BufferedPacket {

    private UUID uniqueId;
    private Query query;
    private boolean sendBack = true;

    @Override
    public void read(PacketReader reader) {
        this.sendBack = reader.readBoolean();
        this.uniqueId = UUID.fromString(reader.readString());
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.sendBack);
        buffer.writeString(uniqueId.toString());
    }

    /**
     * Called when the Packet is received by the other Connection
     *
     * @param source the JsonObject that will handle the Query
     */
    public abstract void handleQuery(VsonObject source);
}
