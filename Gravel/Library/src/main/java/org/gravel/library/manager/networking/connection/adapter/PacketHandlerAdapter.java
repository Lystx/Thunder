package org.gravel.library.manager.networking.connection.adapter;


import org.gravel.library.manager.networking.connection.packet.Packet;

public abstract class PacketHandlerAdapter {

    public abstract void handle(Packet packet);
}
