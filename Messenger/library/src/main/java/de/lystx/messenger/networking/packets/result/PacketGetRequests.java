package de.lystx.messenger.networking.packets.result;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.packets.PacketInBoundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter @AllArgsConstructor
public class PacketGetRequests extends PacketInBoundHandler<List<String>> {

    private final Account account;

    @Override
    public List<String> handleRead(MessageAPI messageAPI) {
        return messageAPI.getFriendManager().getRequests(this.account);
    }
}
