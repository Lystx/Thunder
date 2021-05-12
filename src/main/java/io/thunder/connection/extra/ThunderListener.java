

package io.thunder.connection.extra;

import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.utils.LogLevel;
import io.thunder.packet.*;
import lombok.SneakyThrows;

import java.io.IOException;

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
     * Called when the {@link ThunderSession}
     * connected successfully
     */
    void handleConnect(ThunderSession session);

    /**
     * Called when the {@link ThunderSession} has handshaked
     * with the other connection mostly used for {@link ThunderClient}
     *
     * @param handshake the handshakePacket
     */
    void handleHandshake(PacketHandshake handshake);

    /**
     * Called when the {@link ThunderConnection} is about to
     * send a {@link Packet} 
     * If you not want it to be send do {@link Packet#setCancelled(boolean)}
     * 
     * @param packet the packet
     */
    void handlePacketSend(Packet packet);

    /**
     * Called when receiving a {@link Packet}
     * Alternative to {@link io.thunder.packet.handler.PacketHandler}
     *
     * @param packet the received packet
     */
    void handlePacketReceive(Packet packet);

    /**
     * Called when the {@link ThunderSession}
     * disconnected successfully
     */
    void handleDisconnect(ThunderSession session);

    /**
     * Handles a raw packet intern
     * @param packet the packet to handle
     * @param thunderConnection the connection it came from
     * @throws IOException if something went wrong
     */
    @SneakyThrows
    default void readPacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
        Packet decodedPacket = thunderConnection.getDecoder().decode(packet, new PacketBuffer(packet), thunderConnection);
        if (decodedPacket == null) {
            Thunder.LOGGER.log(LogLevel.ERROR, "A Packet could not be decoded and was marked as null (Class: " + packet.getClass().getName() + ")");
            return;
        }
        decodedPacket.handle(thunderConnection);
        thunderConnection.getPacketAdapter().handle(decodedPacket);
        this.handlePacketReceive(decodedPacket);
    }



}
