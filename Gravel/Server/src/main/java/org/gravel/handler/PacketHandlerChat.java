package org.gravel.handler;

import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.in.PacketInSendMessage;
import org.gravel.library.manager.networking.packets.out.PacketOutSendMessage;

public class PacketHandlerChat {

    @PacketHandler
    public void handle(PacketInSendMessage packet) {

        for (Chat chat : GravelAPI.getInstance().getChatManager().getChats()) {
            if (chat.getUniqueId().equals(packet.getChatUUID())) {
                chat.getMessages().add(packet.getChatMessage());
                GravelAPI.getInstance().getChatManager().update(chat);

                GravelAPI.getInstance().sendPacket(new PacketOutSendMessage(chat, packet.getChatMessage()));
                break;
            }
        }

    }
}
