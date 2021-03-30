package org.gravel.library.manager.networking.packets.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.user.GravelUser;

import java.util.List;

@Getter @AllArgsConstructor
public class PacketQueryGetChats extends PacketInBoundHandler<List<Chat>> {

    private final GravelUser gravelUser;

    @Override
    public List<Chat> handleRead(GravelAPI gravelAPI) {
        return gravelAPI.getChatManager().getChats(this.gravelUser);
    }
}
