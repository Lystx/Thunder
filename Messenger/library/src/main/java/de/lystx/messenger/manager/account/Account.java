package de.lystx.messenger.manager.account;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.chats.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


@Getter @AllArgsConstructor
public class Account implements Serializable {

    private final int id;
    private final String name;
    private final String password;
    private final String ip;
    private final List<String> friends;
    private final List<Integer> mutes;
    private final long creationDate;


    public List<Chat> getChats() {
        List<Chat> list = new LinkedList<>();

        for (Chat chat : MessageAPI.getInstance().getChats()) {
            if (chat.getAccounts().contains(this.id)) {
                list.add(chat);
            }
        }

        return list;
    }

    public boolean isFriendsWith(String friend) {
        for (String s : this.friends) {
            if (s.equalsIgnoreCase(friend)) {
                return true;
            }
        }
        return false;
    }
}
