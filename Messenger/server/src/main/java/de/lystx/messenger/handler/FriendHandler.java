package de.lystx.messenger.handler;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.in.*;
import de.lystx.messenger.networking.packets.out.PacketOutUpdateAccount;
import de.lystx.messenger.networking.packets.result.PacketUpdateAccount;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

public class FriendHandler {

    @PacketHandler
    public void handle(PacketInFriendRequest packet) {
        MessageAPI.getInstance().getFriendManager().addRequest(MessageAPI.getInstance().getAccountManager().getAccount(packet.getFriend()), packet.getAccount().getName());
    }

    @PacketHandler
    public void handle(PacketInFriendAccept packet) {
        Account friendAccount = MessageAPI.getInstance().getAccountManager().getAccount(packet.getFriend());
        Account account = packet.getAccount();
        MessageAPI.getInstance().getFriendManager().addFriends(packet.getAccount(), packet.getFriend());
        MessageAPI.getInstance().getFriendManager().addFriends(MessageAPI.getInstance().getAccountManager().getAccount(packet.getFriend()), packet.getAccount().getName());
        MessageAPI.getInstance().getFriendManager().removeRequest(packet.getAccount(), packet.getFriend());

        MessageAPI.getInstance().getChatManager().createChat(
                new Chat((MessageAPI.getInstance().getChatManager().getChats().getList().size() + 1), account.getName() + " & " + friendAccount.getName(), UUID.randomUUID(), Arrays.asList(friendAccount.getId(), account.getId()),
                        new LinkedList<>())
                );

    }

    @PacketHandler
    public void handle(PacketInFriendDeny packet) {
        MessageAPI.getInstance().getFriendManager().removeRequest(packet.getAccount(), packet.getFriend());
    }

    @PacketHandler
    public void handle(PacketInFriendMute packet) {
        Account account = packet.getAccount();
        account.getMutes().add(packet.getFriend().getId());
        MessageAPI.getInstance().getAccountManager().update(String.valueOf(account.getId()), account);
        MessageAPI.getInstance().getNettyConnection().sendPacket(new PacketOutUpdateAccount(account));

    }

    @PacketHandler
    public void handle(PacketInFriendMuteRemove packet) {
        Account account = packet.getAccount();
        account.getMutes().remove(packet.getFriend().getId());
        MessageAPI.getInstance().getAccountManager().update(String.valueOf(account.getId()), account);
        MessageAPI.getInstance().getNettyConnection().sendPacket(new PacketOutUpdateAccount(account));

    }

    @PacketHandler
    public void handle(PacketInFriendRemove packet) {
        MessageAPI.getInstance().getFriendManager().removeFriends(packet.getAccount(), packet.getFriend());
        MessageAPI.getInstance().getFriendManager().removeFriends(MessageAPI.getInstance().getAccountManager().getAccount(packet.getFriend()), packet.getAccount().getName());
    }

}
