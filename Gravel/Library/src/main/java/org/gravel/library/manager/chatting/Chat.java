package org.gravel.library.manager.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class Chat implements Serializable {

    private final UUID uniqueId;
    private final String name;
    private final List<Account> users;
    private final List<ChatMessage> messages;


    public boolean isGroup() {
        return this.users.size() > 2;
    }

    public GravelUser getOtherMember(GravelUser pov) {
        for (Account user : this.users) {
            if (!user.getName().equalsIgnoreCase(pov.getAccount().getName())) {
                return GravelAPI.getInstance().getUserManager().getUser(user.getUniqueId());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return (isGroup() ? "[Group] " + name : "[Chat] " + name);
    }
}
