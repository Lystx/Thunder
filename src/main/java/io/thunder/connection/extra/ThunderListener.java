

package io.thunder.connection.extra;

import io.thunder.Thunder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.base.ThunderSession;
import io.thunder.impl.other.ProvidedPacketBuffer;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.impl.PacketHandshake;
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
        Packet decodedPacket;
        PacketBuffer packetBuffer = ProvidedPacketBuffer.newInstance(packet);
        String _class = packetBuffer.readString();
        try {
            decodedPacket = thunderConnection.getDecoder().decode(packet, packetBuffer, thunderConnection, _class);
        } catch (Exception e) {
            Thunder.ERROR_HANDLER.onPacketFailure(packet, _class, e);
            return;
        }
        if (decodedPacket == null) {
            Thunder.ERROR_HANDLER.onPacketFailure(packet, _class, new NullPointerException("The Packet was null"));
            return;
        }

        if (thunderConnection.getPacketCompressors().isEmpty()) {
            for (PacketCompressor packetCompressor : thunderConnection.getPacketCompressors()) {
                decodedPacket = packetCompressor.decompress(decodedPacket);
            }
        }

        decodedPacket.handle(thunderConnection);
        thunderConnection.getPacketAdapter().handle(decodedPacket);
        this.handlePacketReceive(decodedPacket);
    }

    /**
     * Returns an Empty {@link ThunderListener}
     *
     * @return listener without any functions
     */
    static ThunderListener empty() {
        return new ThunderListener() {

            @Override
            public void handleConnect(ThunderSession session) {}

            @Override
            public void handleHandshake(PacketHandshake handshake) {}

            @Override
            public void handlePacketSend(Packet packet) {}

            @Override
            public void handlePacketReceive(Packet packet) {}

            @Override
            public void handleDisconnect(ThunderSession session) {}

        };
    }
}
