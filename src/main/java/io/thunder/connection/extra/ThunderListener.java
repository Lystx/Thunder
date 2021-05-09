

package io.thunder.connection.extra;

import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.packet.*;
import io.thunder.packet.response.PacketRespond;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * This class is used to handle specific
 * actions from both connection types
 * that means the {@link ThunderListener} is suitable
 * for both {@link ThunderClient} and {@link ThunderServer}
 * only the {@link ThunderConnection} will be something different from
 * one of the types
 */
public interface ThunderListener {


    /**
     * Called when a Packet gets handled
     * This packet can be a raw {@link Packet}
     *
     * @param packet the received Packet
     * @param thunderConnection the connection it came from (Server or Client)
     * @throws IOException if something went wrong
     */
    void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException;


    /**
     * Server:
     *    -> Called when a Client connects to the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when connected to the server
     *     @param thunderConnection > The Client itsself
     */
    void handleConnect(ThunderConnection thunderConnection);

    /**
     * Server:
     *    -> Called when a Client disconnects from the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when disconnected from the server
     *     @param thunderConnection > The Client
     */
    void handleDisconnect(ThunderConnection thunderConnection);

    /**
     * Handles a raw packet intern
     * @param packet the packet to handle
     * @param thunderConnection the connection it came from
     * @throws IOException if something went wrong
     */
    @SneakyThrows
    default void readPacket(Packet packet, ThunderConnection thunderConnection) throws IOException {

        Packet decodedPacket = thunderConnection.getDecoder().decode(packet, new PacketBuffer(packet), thunderConnection);

        decodedPacket.handle(thunderConnection);
        thunderConnection.getPacketAdapter().handle(decodedPacket);

        if (decodedPacket instanceof PacketRespond) {
            return;
        }
        this.handlePacket(decodedPacket, thunderConnection); //Then handling the ready built packet
    }



}
