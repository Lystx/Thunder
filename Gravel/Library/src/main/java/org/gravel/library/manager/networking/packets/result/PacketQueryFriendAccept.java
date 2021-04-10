package org.gravel.library.manager.networking.packets.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutNotify;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;

import javax.swing.*;

@Getter @AllArgsConstructor

public class PacketQueryFriendAccept extends PacketInBoundHandler<Boolean> {

    private final GravelUser executor;
    private final GravelUser friendToDeny;

    @Override
    public Boolean handleRead(GravelAPI gravelAPI) {
        executor.getRequests().removeIf(account -> account.getName().equalsIgnoreCase(friendToDeny.getAccount().getName()));


        executor.getFriends().add(friendToDeny.getAccount());
        friendToDeny.getFriends().add(executor.getAccount());

        friendToDeny.getRequests().removeIf(account -> account.getName().equalsIgnoreCase(executor.getAccount().getName()));
        executor.getRequests().removeIf(account -> account.getName().equalsIgnoreCase(friendToDeny.getAccount().getName()));

        gravelAPI.sendPacket(new PacketOutNotify(friendToDeny, "You are now friends with " + executor.getAccount().getName(), "Gravel | Friends", JOptionPane.INFORMATION_MESSAGE));
        gravelAPI.sendPacket(new PacketOutNotify(executor, "You are now friends with " + friendToDeny.getAccount().getName(), "Gravel | Friends", JOptionPane.INFORMATION_MESSAGE));

        gravelAPI.getUserManager().update(friendToDeny);
        gravelAPI.getUserManager().update(executor);
        gravelAPI.sendPacket(new PacketOutUpdatePlayer(friendToDeny));
        gravelAPI.sendPacket(new PacketOutUpdatePlayer(executor));
        gravelAPI.reload();

        return true;
    }
}
