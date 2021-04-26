package io.thunder.connection;

import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.featured.Query;
import io.thunder.manager.packet.featured.QueryPacket;
import io.thunder.manager.packet.handler.PacketAdapter;
import io.thunder.manager.packet.handler.PacketHandler;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.SneakyThrows;

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
     * Sets the name of this {@link ThunderConnection}
     * for identification later
     *
     * @param name the name of the conneciton
     */
    void setName(String name);

    /**
     * Sends a packet from the current connection
     * to the (server / client)
     * @param packet the packet to send
     */
    void sendPacket(Packet packet);

    default void sendPacket(ThunderPacket packet) {
        this.sendPacket(transform(packet));
    }

    static Packet transform(ThunderPacket packet) {
        if (packet instanceof BufferedPacket) {
            return fromBuffered(((BufferedPacket)packet));
        } else {
            return fromAbstract(packet);
        }
    }


    default ThunderConnection addPacketHandler(PacketHandler packetHandler) {
        this.getPacketAdapter().addHandler(packetHandler);
        return this;
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
        PacketBuffer packetBuffer = new PacketBuffer();
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

        VsonObject vsonObject = new VsonObject();
        vsonObject.append("_class", packet.getClass().getName());
        vsonObject.append("_processingTime", System.currentTimeMillis());

        vsonObject.append("_abstractPacket", packet);

        PacketBuffer packetBuffer = new PacketBuffer();
        packetBuffer.writeString(vsonObject.toString(FileFormat.RAW_JSON));

        return packetBuffer.build();
    }

    /**
     * Disconnects the connection
     * either from the server or
     * to to the server
     */
    void disconnect();

    /**
     * Every connection has it's own Session
     * to identify it. This can be useful for Developers
     * to check Sessions and so on.
     *
     * @return the Session of this Connection
     */
    ThunderSession getSession();

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
