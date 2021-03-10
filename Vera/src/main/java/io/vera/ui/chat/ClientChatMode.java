
package io.vera.ui.chat;

import lombok.Getter;


public enum ClientChatMode {

    CHAT_AND_COMMANDS(0),
    COMMANDS_ONLY(1),
    NONE(2);

    @Getter
    private final int data;

    ClientChatMode(int data) {
        this.data = data;
    }

    public static ClientChatMode of(int data) {
        for (ClientChatMode chatMode : values()) {
            if (chatMode.getData() == data) {
                return chatMode;
            }
        }

        throw new IllegalArgumentException("no client chat mode with id=" + data);
    }
}