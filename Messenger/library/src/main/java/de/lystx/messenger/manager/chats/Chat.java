package de.lystx.messenger.manager.chats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class Chat implements Serializable {

    private final int id;
    private final String name;
    private final UUID uniqueId;
    private final List<Integer> accounts;
    private final List<ChatMessage> messages;

    public boolean isGroup() {
        return this.accounts.size() >= 3;
    }
}
