package io.thunder.manager.packet.handler;

import io.thunder.manager.packet.Packet;

/**
 * This Interface is used to handle
 * incoming Packets
 */
public interface PacketHandler {

    /**
     * Called to handle the given Packet
     * you can check if the packet is instance of
     * another Packet you're trying to work with and then
     * simply cast it to the Packet you want
     *
     * @param packet the given Packet
     */
    void handle(Packet packet);
}
