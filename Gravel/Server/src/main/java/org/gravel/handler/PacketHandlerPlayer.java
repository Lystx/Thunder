package org.gravel.handler;

import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;

public class PacketHandlerPlayer {


    @PacketHandler
    public void handle(PacketOutUpdatePlayer packet) {
        final GravelUser gravelUser = packet.getGravelUser();
        GravelAPI.getInstance().getUserManager().update(gravelUser);
        GravelAPI.getInstance().sendPacket(packet);
        GravelAPI.getInstance().reload();
    }
}
