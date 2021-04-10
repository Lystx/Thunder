package io.thunder.manager.packet.featured;

import com.google.gson.JsonObject;
import io.thunder.manager.packet.BufferedPacket;
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

    /**
     * Called when the Packet is received by the other Connection
     *
     * @param source the JsonObject that will handle the Query
     */
    public abstract void handleQuery(JsonObject source);
}
