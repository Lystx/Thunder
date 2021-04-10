package org.gravel.library.manager.networking.packets.result;

import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.user.GravelUser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;


@Getter
public class PacketQueryGetChat extends PacketInBoundHandler<Chat> {

    private final Account[] gravelUser;
    private final String name;


    public PacketQueryGetChat(String name, Account... gravelUser) {
        this.gravelUser = gravelUser;
        this.name = name;
    }

    @Override
    public Chat handleRead(GravelAPI gravelAPI) {

        Chat chat = gravelAPI.getChatManager().getChat(this.name);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.gravelUser.length; i++) {
            stringBuilder.append(this.gravelUser[i].getName());
            if (i != this.gravelUser.length -1) {
                stringBuilder.append(" & ");
            }
        }

        if (chat == null) {
            gravelAPI.getChatManager().addChat(new Chat(
                    UUID.randomUUID(),
                    stringBuilder.toString(),
                    Arrays.asList(this.gravelUser),
                    new LinkedList<>()
            ));
            chat = gravelAPI.getChatManager().getChat(this.name);
        }

        return chat;
    }
}
