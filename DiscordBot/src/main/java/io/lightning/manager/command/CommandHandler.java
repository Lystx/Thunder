package io.lightning.manager.command;

import lombok.Getter;
import net.dv8tion.jda.api.entities.*;

@Getter
public abstract class CommandHandler {

    private final String name;
    private final String description;
    private final String[] aliases;
    private final CommandCategory category;

    public CommandHandler(String name, String description, CommandCategory category, String... aliases) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.aliases = aliases;
    }

    public abstract boolean hasPermission(Member member);

    public abstract void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild);

    public abstract void syntax(String command, TextChannel channel, User executor);
}
