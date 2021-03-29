package de.lystx.messenger.networking.packets.in;

import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketInCreateGroup extends Packet implements Serializable {

    private final List<Account> accounts;
}
