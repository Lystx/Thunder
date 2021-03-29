package de.lystx.messenger.commands;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.chats.ChatMessage;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NavigateCommand extends Command {

    public NavigateCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {
        if (args.length != 1) {
            help(console);
            return;
        }

        try {
            int i = Integer.parseInt(args[0]);
            Chat chat = Client.getInstance().getChats().stream().filter(chat1 -> chat1.getId() == i).findFirst().orElse(null);
            if (chat == null) {
                console.sendMessage("ERROR", "§cYou aren't in a chat with the ID &e" + i + "§c!");
                return;
            }

            MessageAPI.getInstance().setCurrentChat(chat);

            console.clearScreen();
            console.sendMessage("INFO", "§fYou joined Chat &e" + chat.getName() + " §fwith §a" + chat.getAccounts().size() + " Members§8!");
            console.sendMessage("INFO", "§fYou can §cexit §fit at any time by typing §c-l§8!");

            for (ChatMessage message : chat.getMessages()) {
               message.display(console);
            }
        } catch (NumberFormatException e) {
            console.sendMessage("ERROR", "§cPlease provide a valid number!");
        }

    }

    @Override
    public void help(Console console) {
        console.sendMessage("ERROR", "§cnavigate <chat-id>");
    }
}
