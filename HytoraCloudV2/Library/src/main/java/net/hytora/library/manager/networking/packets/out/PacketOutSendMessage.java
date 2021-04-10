package net.hytora.library.manager.networking.packets.out;

import net.hytora.library.elements.sender.CloudPlayer;
import net.hytora.library.manager.networking.connection.packet.Packet;

public class PacketOutSendMessage extends Packet {

    public PacketOutSendMessage(CloudPlayer cloudPlayer, String message) {
        this.put("player", cloudPlayer.getName());
        this.put("message", message);
    }


}
