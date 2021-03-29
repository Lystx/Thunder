package de.lystx.messenger.manager.chats;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.packets.in.PacketInSendMessage;
import de.lystx.messenger.networking.packets.out.PacketOutCreateChat;
import de.lystx.messenger.util.Appender;
import lombok.Getter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ChatManager {

    private final Appender<Chat> chats;

    public ChatManager() {
        this.chats = new Appender<>(Chat.class, "chats");
        this.chats.setAccess(true);
    }

    public void createChat(Chat chat) {
        this.chats.append(chat.getUniqueId().toString(), chat);
        MessageAPI.getInstance().getNettyConnection().sendPacket(new PacketOutCreateChat(chat));
    }

    public List<Chat> getChats(Account account) {
        List<Chat> chats = new LinkedList<>();
        for (Chat chat : this.chats) {
            if (chat.getAccounts().contains(account.getId())) {
                chats.add(chat);
            }
        }
        return chats;
    }

    public void execute(String[] strings) {
        Chat chat = MessageAPI.getInstance().getCurrentChat();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            if (i != strings.length -1) {
                stringBuilder.append(" ");
            }
        }

        String message = stringBuilder.toString();

        if (message.trim().isEmpty()) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage(chat.getId(), MessageAPI.getInstance().getAccount().getName(), message, new Date().getTime());

        MessageAPI.getInstance().getNettyConnection().sendPacket(new PacketInSendMessage(chatMessage));
    }
}
