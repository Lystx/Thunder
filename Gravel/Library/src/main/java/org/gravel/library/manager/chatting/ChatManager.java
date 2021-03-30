package org.gravel.library.manager.chatting;

import org.gravel.library.manager.user.GravelUser;

import java.util.List;
import java.util.UUID;

public interface ChatManager {

    List<Chat> getChats();

    Chat getChat(String name);

    Chat getChat(UUID uniqueId);

    void update(Chat chat);

    List<Chat> getChats(GravelUser gravelUser);

    void addChat(Chat chat);

}
