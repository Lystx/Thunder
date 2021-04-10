package io.thunder.connection;

import com.google.gson.JsonObject;
import io.thunder.Thunder;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.featured.Query;
import io.thunder.manager.packet.featured.QueryPacket;
import io.thunder.manager.packet.handler.PacketAdapter;
import lombok.SneakyThrows;

import java.util.UUID;

/**
 * This interface is used to define the {@link ThunderServer}
 * and the {@link ThunderClient}
 *
 * You can also use a ThunderConnection as variable and be able
 * to insert both {@link ThunderClient} and {@link ThunderServer}
 * because they extend from this interface
 */
public interface ThunderConnection {

    /**
     * Adds a {@link ThunderListener} to the current
     * Connection
     *
     * @param serverListener the Listener to add
     */
    void addHandler(ThunderListener serverListener);

    /**
     * Sends a packet from the current connection
     * to the (server / client)
     * @param packet the packet to send
     */
    void sendPacket(Packet packet);

    default void sendPacket(ThunderPacket packet) {
        if (packet instanceof BufferedPacket) {
            this.sendPacket(fromBuffered(((BufferedPacket)packet)));
        } else {
            this.sendPacket(fromAbstract(packet));
        }
    }

    /**
     * @return the PacketAdapter
     */
    PacketAdapter getPacketAdapter();

    /**
     * Sends a {@link Query} to receive
     * Realtime-Results
     *
     * @param packet the QueryPacket
     * @return the Query
     */
    Query sendQuery(QueryPacket packet);

    /**
     * Transforms a {@link BufferedPacket} into a
     * normal and raw {@link Packet}
     *
     * @param packet the packet to transform
     * @return the raw packet
     */
    @SneakyThrows
    static Packet fromBuffered(BufferedPacket packet) {
        PacketBuffer packetBuffer = new PacketBuffer(packet.getPacketID());
        packetBuffer.writeString(packet.getClass().getName());
        packet.write(packetBuffer);
        return packetBuffer.build();
    }

    /**
     * Transforms a {@link ThunderPacket} into a
     * normal and raw {@link Packet}
     *
     * @param packet the packet to transform
     * @return the raw packet
     */
    @SneakyThrows
    static Packet fromAbstract(ThunderPacket packet) {

        JsonObject vsonObject = new JsonObject();
        vsonObject.addProperty("_id", packet.getPacketID());
        vsonObject.addProperty("_class", packet.getClass().getName());
        vsonObject.addProperty("_processingTime", System.currentTimeMillis());

        vsonObject.add("_abstractPacket", Thunder.GSON.toJsonTree(packet));

        PacketBuffer packetBuffer = new PacketBuffer(packet.getPacketID());
        packetBuffer.writeString(vsonObject.toString());

        return packetBuffer.build();
    }

    /**
     * Disconnects the connection
     * either from the server or
     * to to the server
     */
    void disconnect();

    /**
     * Every connection has it's own unique ID
     * to identify it. This can be useful for Developers
     * to check Sessions and so on.
     *
     * @return the UUID of this Connection
     */
    UUID getUniqueId();

    /**
     * @return Information on this connection
     */
    String asString();

    /**
     * Checks if the Connection is still stable
     * @return boolean if connected
     */
    boolean isConnected();

}
