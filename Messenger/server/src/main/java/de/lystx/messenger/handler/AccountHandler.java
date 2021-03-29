package de.lystx.messenger.handler;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.Server;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.in.PacketInCreateAccount;
import de.lystx.messenger.networking.packets.in.PacketInLogin;
import de.lystx.messenger.networking.packets.out.PacketOutAccountResult;
import de.lystx.messenger.networking.packets.out.PacketOutLogin;
import io.vson.elements.object.VsonObject;

import java.util.Date;
import java.util.LinkedList;

public class AccountHandler {

    @PacketHandler
    public void handle(PacketInCreateAccount packet) {
        String password = packet.getPassword();
        String rePassword = packet.getPasswordRe();
        String name = packet.getName();

        VsonObject result = new VsonObject();

        if (!password.equals(rePassword)) {
            result.append("allow", false);
            result.append("message", "&cPasswords do not match!");
        } else if (MessageAPI.getInstance().getAccountManager().getAccount(name) != null) {
            result.append("allow", false);
            result.append("message", "&cAccount with name &e" + name + " &calready exists!");

        } else {
            final int id = MessageAPI.getInstance().getAccountManager().getList().size() + 1;
            MessageAPI.getInstance().getAccountManager().append(String.valueOf(id), new Account(id, name.trim(), password.trim(), packet.getIp(), new LinkedList<>(), new LinkedList<>(), new Date().getTime()));
            result.append("allow", true);
            result.append("message", "&fAccount with name &e" + name + " &8[&e#" + id + "&8] &fwas created&8!");
        }

        Server.getInstance().getConnectionServer().sendPacket(new PacketOutAccountResult(result));

    }


    @PacketHandler
    public void handle(PacketInLogin packet) {
        final String name = packet.getName();
        final String password = packet.getPassword();

        Account account = MessageAPI.getInstance().getAccountManager().getAccount(name);

        if (account == null) {
            Server.getInstance().getConnectionServer().sendPacket(new PacketOutLogin(null, false, new LinkedList<>(), new LinkedList<>()));
        } else {
            if (password.equals(account.getPassword())) {
                Server.getInstance().getConsole().sendMessage("INFO", "&e" + account.getName().trim() + " &flogged in from &e" + account.getIp());
                Server.getInstance().getConnectionServer().sendPacket(new PacketOutLogin(account, true, MessageAPI.getInstance().getAccountManager().getList(), MessageAPI.getInstance().getChatManager().getChats(account)));
            } else {
                Server.getInstance().getConnectionServer().sendPacket(new PacketOutLogin(null, false, new LinkedList<>(), new LinkedList<>()));
            }
        }
    }
}
