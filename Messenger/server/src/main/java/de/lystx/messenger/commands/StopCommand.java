package de.lystx.messenger.commands;

import de.lystx.messenger.Server;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;

public class StopCommand extends Command {

    public StopCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {
        console.sendMessage("TEST", "&cShutting down &eMessenger&c...");
        Server.getInstance().shutdown();
    }

    @Override
    public void help(Console console) {

    }
}
