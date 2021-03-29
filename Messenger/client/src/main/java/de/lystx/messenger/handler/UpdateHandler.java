package de.lystx.messenger.handler;

import de.lystx.messenger.Client;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.out.PacketOutUpdateAccount;

public class UpdateHandler {

    @PacketHandler
    public void handle(PacketOutUpdateAccount packet) {
        Account account = packet.getAccount();
        if (account.getName().equalsIgnoreCase(Client.getInstance().getAccount().getName())) {
            Client.getInstance().setAccount(account);
        }
    }
}
