package io.betterbukkit.provider.command;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public final class CommandInfo {

    private final String name;
    private final String description;
    private final String[] aliases;
    private final String permission;
    private final String permissionMessage;

}
