package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutLogin extends Packet implements Serializable {

    private final Account account;
    private final boolean allow;
    private final List<Account> accounts;
    private final List<Chat> chats;


}
