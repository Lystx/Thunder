package org.gravel.library.manager.networking.packets.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;

@Getter @AllArgsConstructor

public class PacketQueryFriendDeny extends PacketInBoundHandler<Boolean> implements Serializable {

    private final GravelUser executor;
    private final GravelUser friendToDeny;

    @Override
    public Boolean handleRead(GravelAPI gravelAPI) {
        executor.getRequests().removeIf(account -> account.getName().equalsIgnoreCase(friendToDeny.getAccount().getName()));
        gravelAPI.getUserManager().update(executor);
        gravelAPI.sendPacket(new PacketOutUpdatePlayer(executor));
        gravelAPI.reload();

        return true;
    }
}
