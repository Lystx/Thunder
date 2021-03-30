package org.gravel.handler;

import lombok.AllArgsConstructor;
import org.gravel.GravelClient;
import org.gravel.elements.gui.Gui;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutSendMessage;
import org.gravel.library.manager.networking.packets.result.PacketQueryGetChats;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PacketHandlerChat {

    private final GravelClient gravelClient;

    @PacketHandler
    public void handle(PacketOutSendMessage packet) {
        final Chat chat = packet.getChat();

        final List<Chat> result = GravelAPI.getInstance().sendQuery(new PacketQueryGetChats(this.gravelClient.getUser())).getResult();
        if (result == null || result.isEmpty()) {
            return;
        }
        for (Chat chat1 : result) {
            if (chat1.getName().equalsIgnoreCase(chat.getName())) {
                GravelAPI.getInstance().updateChat(chat, Gui.clientLabel, Gui.textOutput, this.gravelClient.getUser());
                break;
            }
        }
    }
}
