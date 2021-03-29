package de.lystx.messenger.commands;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.networking.packets.in.PacketInCreateGroup;

import java.util.LinkedList;
import java.util.List;

public class HubCommand extends Command {

    public HubCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {
        Client.getInstance().mainMenu(MessageAPI.getInstance().getAccounts(), Client.getInstance().getChats());
    }

    @Override
    public void help(Console console) {

    }
}
