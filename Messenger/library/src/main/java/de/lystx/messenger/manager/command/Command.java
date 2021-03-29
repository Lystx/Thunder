package de.lystx.messenger.manager.command;

import de.lystx.messenger.manager.console.Console;
import lombok.Getter;

@Getter
public abstract class Command {

    private final String name;
    private final String description;
    private final String[] aliases;

    public Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(Console console, String[] args);


    public abstract void help(Console console);
}
