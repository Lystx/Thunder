

package io.thunder.connection.extra;

import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.*;
import io.thunder.packet.object.PacketObject;
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
     * Called when the {@link ThunderSession}
     * connected successfully
     */
    void handleConnect(ThunderSession session);

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

    }



}
