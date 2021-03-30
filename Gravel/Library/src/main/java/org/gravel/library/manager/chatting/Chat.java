package org.gravel.library.manager.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class Chat implements Serializable {

    private final UUID uniqueId;
    private final String name;
    private final List<GravelUser> users;
    private final List<ChatMessage> messages;
}
