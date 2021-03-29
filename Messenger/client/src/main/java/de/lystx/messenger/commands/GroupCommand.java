package de.lystx.messenger.commands;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.networking.packets.in.PacketInCreateGroup;

import java.util.LinkedList;
import java.util.List;

public class GroupCommand extends Command {

    public GroupCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {

        if (args.length == 0) {
            help(console);
            return;
        }
        List<Account> accounts = new LinkedList<>();
        for (String arg : args) {
            final Account account = MessageAPI.getInstance().getAccount(arg);
            if (account == null) {
                console.sendMessage("ERROR", "§cThere is no existing account with the name §e" + arg + "§c!");
                continue;
            }
            if (!Client.getInstance().getAccount().isFriendsWith(account.getName())) {
                console.sendMessage("ERROR", "§cCouldn't add §e" + account.getName() + " §cto group because you are not friends with eachother!");
                continue;
            }
            accounts.add(account);
        }
        accounts.add(Client.getInstance().getAccount());
        if (accounts.size() <= 2) {
            console.sendMessage("ERROR", "§cCan't create §eGroup §cwith §e2 Members§c! Just start a private chat!");
            return;
        }
        MessageAPI.getInstance().getNettyConnection().sendPacket(new PacketInCreateGroup(accounts));
        console.sendMessage("INFO", "§aCreated group!");
    }

    @Override
    public void help(Console console) {

    }
}
