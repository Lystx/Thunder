package org.gravel.library.manager.networking.connection.adapter;


import io.thunder.manager.packet.ThunderPacket;

public abstract class PacketHandlerAdapter {

    public abstract void handle(ThunderPacket packet);
}
