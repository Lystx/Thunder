package de.lystx.messenger.networking.packets.result;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.networking.netty.NettyConnection;
import de.lystx.messenger.networking.packets.PacketInBoundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter @AllArgsConstructor
public class PacketGetChats extends PacketInBoundHandler<List<Chat>> {

    private final Account account;

    @Override
    public List<Chat> handleRead(MessageAPI messageAPI) {
        return messageAPI.getChatManager().getChats(this.account);
    }

}
