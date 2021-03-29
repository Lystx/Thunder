package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutUpdateAccount extends Packet implements Serializable {

    private final Account account;

}
