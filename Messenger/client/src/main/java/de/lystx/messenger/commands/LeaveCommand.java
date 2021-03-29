package de.lystx.messenger.commands;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;


public class LeaveCommand extends Command {

    public LeaveCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {
        Chat chat = MessageAPI.getInstance().getCurrentChat();
        if (chat == null) {
            console.sendMessage("ERROR", "§cYou are not in a chat at the moment!");
            return;
        }

        Client.getInstance().mainMenu(MessageAPI.getInstance().getAccounts(), Client.getInstance().getChats());
        console.sendMessage("INFO", "§cYou left Chat &e" + chat.getName() + "&c!");

        MessageAPI.getInstance().setCurrentChat(null);
    }

    @Override
    public void help(Console console) {

    }
}
