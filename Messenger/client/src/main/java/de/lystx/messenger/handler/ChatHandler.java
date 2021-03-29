package de.lystx.messenger.handler;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.chats.ChatMessage;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.out.PacketOutCreateChat;
import de.lystx.messenger.networking.packets.out.PacketOutSendMessage;

public class ChatHandler {


    @PacketHandler
    public void handle(PacketOutSendMessage packet) {
        final ChatMessage chatMessage = packet.getChatMessage();
        final Chat chat = packet.getChat();
        if (chat == null) {
            return;
        }

        if (MessageAPI.getInstance().getCurrentChat() != null) {
            if (chat.getId() == MessageAPI.getInstance().getCurrentChat().getId()) {
                chatMessage.display(Client.getInstance().getConsole());
            }
        }
        try {
            Account sender = chatMessage.getSender();
            if (!MessageAPI.getInstance().getAccount().getMutes().contains(sender.getId() + "")) {
                if (sender.getName().equalsIgnoreCase(Client.getInstance().getAccount().getName())) {
                    return;
                }
                if (MessageAPI.getInstance().getCurrentChat() != null) {
                    if (MessageAPI.getInstance().getCurrentChat().getId() != chat.getId()) {
                        MessageAPI.getInstance().sendNotification("Messenger", chat.getName(), sender.getName() + ": " + chatMessage.getContent());
                    }
                } else {
                    MessageAPI.getInstance().sendNotification("Messenger", chat.getName(), sender.getName() + ": " + chatMessage.getContent());
                }
            }
        } catch (NullPointerException e) {

        }
    }

    @PacketHandler
    public void handle(PacketOutCreateChat packet) {
        Client.getInstance().getChats().add(packet.getChat());
    }

}
