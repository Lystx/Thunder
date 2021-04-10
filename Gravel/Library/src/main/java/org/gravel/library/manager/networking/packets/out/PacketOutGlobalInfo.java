package org.gravel.library.manager.networking.packets.out;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends ThunderPacket implements Serializable {

    private final List<GravelUser> gravelUsers;
    private final List<Account> accounts;

    @Override
    public int getPacketID() {
        return 0x01;
    }
}
