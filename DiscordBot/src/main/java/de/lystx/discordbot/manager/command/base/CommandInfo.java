package de.lystx.discordbot.manager.command.base;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;

@Getter @AllArgsConstructor
public final class CommandInfo {

    private final String name;
    private final String description;
    private final String[] aliases;
    private final Permission permission;

}
