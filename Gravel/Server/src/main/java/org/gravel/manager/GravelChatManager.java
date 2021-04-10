package org.gravel.manager;

import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatManager;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.utils.Appender;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GravelChatManager extends Appender<Chat> implements ChatManager {

    public GravelChatManager(File save) {
        super(Chat.class, save);
        this.access = true;
    }

    @Override
    public List<Chat> getChats() {
        return this.getList();
    }

    @Override
    public Chat getChat(String name) {
        return this.getList().stream().filter(chat -> chat.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Chat getChat(UUID uniqueId) {
        return this.getList().stream().filter(chat -> chat.getUniqueId() == uniqueId).findFirst().orElse(null);
    }

    @Override
    public void update(Chat chat) {
        this.update(chat.getUniqueId().toString(), chat);
    }

    @Override
    public List<Chat> getChats(GravelUser gravelUser) {
        List<Chat> chats = new LinkedList<>();
        for (Chat chat : this.getChats()) {
            for (Account user : chat.getUsers()) {
                if (user.getName().equalsIgnoreCase(gravelUser.getAccount().getName())) {
                    chats.add(chat);
                }
            }
        }
        return chats;
    }

    @Override
    public void addChat(Chat chat) {
        this.append(chat.getUniqueId().toString(), chat);
    }
}
