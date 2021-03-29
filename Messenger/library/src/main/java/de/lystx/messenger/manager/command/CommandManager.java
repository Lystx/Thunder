package de.lystx.messenger.manager.command;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.console.Console;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class CommandManager {

    private final List<Command> commands;
    private Console console;

    public CommandManager() {
        this.console = null;
        this.commands = new LinkedList<>();
    }

    public void registerCommand(Command command) {
        this.commands.add(command);
    }

    public void execute(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : args) {
            stringBuilder.append(s).append(" ");
        }
        if (stringBuilder.toString().trim().isEmpty()) {
            this.console.sendMessage("ERROR", "&cPlease provide something!");
            return;
        }
        String command = args[0];
        Command cmd = this.getCommand(command);

        if (cmd == null) {
            if (MessageAPI.getInstance().getCurrentChat() == null) {
                this.console.sendMessage("ERROR", "&cThe command &e" + command + " &cdoes not exist!");
            } else {
                MessageAPI.getInstance().getChatManager().execute(args);
            }
            return;
        }

        List<String> list = new LinkedList<>(Arrays.asList(args).subList(1, args.length));
        List<String> newList = new LinkedList<>();
        for (String argument : list) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                newList.add(argument);
            }
        }
        cmd.execute(this.console, newList.toArray(new String[0]));
    }


    public Command getCommand(String commandName) {
        for (Command commandInfo : this.commands) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

}
