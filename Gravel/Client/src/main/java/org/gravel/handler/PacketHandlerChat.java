package org.gravel.handler;

import lombok.AllArgsConstructor;
import org.gravel.GravelClient;
import org.gravel.elements.gui.screen.MainGUi;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutSendMessage;

@AllArgsConstructor
public class PacketHandlerChat {

    private final GravelClient gravelClient;

    @PacketHandler
    public void handle(PacketOutSendMessage packet) {
        final Chat chat = packet.getChat();
        if (GravelAPI.getInstance().getCurrentChat().getName().equalsIgnoreCase(chat.getName())) {
            GravelAPI.getInstance().updateChat(chat, MainGUi.playerLabel, MainGUi.textField3, this.gravelClient.getUser());
        }
    }
}
