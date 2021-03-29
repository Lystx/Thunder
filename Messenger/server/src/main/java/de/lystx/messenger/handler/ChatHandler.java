package de.lystx.messenger.handler;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.chats.ChatMessage;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.in.PacketInCreateGroup;
import de.lystx.messenger.networking.packets.in.PacketInSendMessage;
import de.lystx.messenger.networking.packets.out.PacketOutSendMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChatHandler {


    @PacketHandler
    public void handle(PacketInSendMessage packet) {
        final ChatMessage chatMessage = packet.getChatMessage();
        final Chat chat = MessageAPI.getInstance().getChatManager().getChats().getList().stream().filter(chat1 -> chat1.getId() == chatMessage.getChatID()).findFirst().orElse(null);
        if (chat == null) {
            System.out.println("CHAT NULL");
            return;
        }
        chat.getMessages().add(chatMessage);
        MessageAPI.getInstance().getChatManager().getChats().update(chat.getUniqueId().toString(), chat);

        PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(chat, chatMessage);
        MessageAPI.getInstance().getNettyConnection().sendPacket(packetOutSendMessage);
   }


   @PacketHandler
    public void handle(PacketInCreateGroup packet) {
       final List<Account> accounts = packet.getAccounts();

       StringBuilder stringBuilder = new StringBuilder();

       for (int i = 0; i < accounts.size(); i++) {
           stringBuilder.append(accounts.get(i).getName());
           if (i != accounts.size() - 1) {
               stringBuilder.append(" & ");
           }
       }

       String chatName = stringBuilder.toString();

       List<Integer> list = new LinkedList<>();

       for (Account account : accounts) {
          list.add(account.getId());
       }

       Chat chat = new Chat(MessageAPI.getInstance().getChatManager().getChats().getList().size() + 1, chatName, UUID.randomUUID(), list, new LinkedList<>());
       MessageAPI.getInstance().getChatManager().createChat(chat);
   }
}
