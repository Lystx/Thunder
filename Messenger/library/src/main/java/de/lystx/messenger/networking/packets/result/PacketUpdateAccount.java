package de.lystx.messenger.networking.packets.result;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.packets.PacketInBoundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter @AllArgsConstructor
public class PacketUpdateAccount extends PacketInBoundHandler<Account> {

    private final Account account;

    @Override
    public Account handleRead(MessageAPI messageAPI) {
        return messageAPI.getAccountManager().getAccount(this.account.getName());
    }
}
