package de.lystx.messenger.networking.connection.adapter;

import de.lystx.messenger.networking.connection.packet.Packet;

public abstract class PacketHandlerAdapter {

    /**
     * Handles the incoming packet
     * @param packet
     */
    public abstract void handle(Packet packet);

}
