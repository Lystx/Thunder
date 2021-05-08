package io.thunder.connection;

import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.handler.PacketAdapter;
import io.thunder.manager.packet.handler.PacketHandler;
import io.thunder.manager.packet.response.PacketRespond;
import io.thunder.manager.packet.response.Response;
import io.thunder.manager.packet.response.ResponseStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

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

    default ThunderConnection addPacketHandler(PacketHandler packetHandler) {
        this.getPacketAdapter().addHandler(packetHandler);
        return this;
    }

    /**
     * Calls the {@link ThunderConnection#transferToResponse(Packet)} Method
     * It just simplify's it into a {@link Consumer} to work with it
     *
     * @param packet the Packet to send
     * @param consumer the Consumer to work with
     */
    default void sendPacket(Packet packet, Consumer<Response> consumer) {
        consumer.accept(this.transferToResponse(packet));
    }

    /**
     * Calls the {@link ThunderConnection#sendPacket(Packet)} Method
     * This will wait for the given Packet to respond
     * And if the {@link Packet} responded it will return its {@link Response}
     * to work with it
     *
     * @param pp the Packet await Response for
     * @return the Response the Packet gets
     */
    default Response transferToResponse(Packet pp) {
        Response[] response = {null};
        getPacketAdapter().addHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getUniqueId().equals(pp.getUniqueId())) {
                        response[0] = new Response(packetRespond);
                        getPacketAdapter().removeHandler(this);
                    }
                }
            }
        });

        this.sendPacket(pp);

        int count = 0;
        while (response[0] == null && count++ < 3000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        if (count >= 2999) {
            response[0] = new Response(new PacketRespond("The Request timed out", ResponseStatus.FAILED));
        }
        return response[0];
    }

    /**
     * @return the PacketAdapter
     */
    PacketAdapter getPacketAdapter();

    /**
     * Flushes the Connection
     * and clears every Packet thats still
     * in the ConnectionGateway
     */
    void flush();

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


    /**
     * Writes a packet to a Destination (Mostly another {@link ThunderClient}
     * or {@link ThunderServer} and sets the ProcessingTime of the packet to now
     *
     * @param dataOutputStream the {@link OutputStream} to write the Packet to
     * @throws IOException
     */
    static void processOut(Packet packet, DataOutputStream dataOutputStream) throws IOException {

        PacketBuffer packetBuffer = new PacketBuffer();
        packetBuffer.writeString(packet.getClass().getName());
        packet.write(packetBuffer);
        packet.setData(packetBuffer.build());

        packet.setProcessingTime(System.currentTimeMillis());

        dataOutputStream.writeLong(packet.getProcessingTime());
        dataOutputStream.writeInt(packet.getProtocolId());
        dataOutputStream.writeInt(packet.getProtocolVersion());
        dataOutputStream.writeLong(packet.getUniqueId().getLeastSignificantBits());
        dataOutputStream.writeLong(packet.getUniqueId().getMostSignificantBits());
        dataOutputStream.writeInt(packet.getData().length);
        dataOutputStream.write(packet.getData());
    }
}
