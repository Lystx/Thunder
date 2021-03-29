package io.betterbukkit.provider.command;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.player.CommandSender;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter @Setter
public final class CommandProvider {

    private final Map<String, List<Method>> commandClasses;
    private final List<CommandInfo> commandInfos;
    private final Map<String, Object> invokers;
    private boolean active;

    public CommandProvider(EasyBukkit easyBukkit) {
        this.commandClasses = new HashMap<>();
        this.invokers = new HashMap<>();
        this.active = true;
        this.commandInfos = new LinkedList<>();
    }

    public void registerCommand(Object classObject) {
        for (Method declaredMethod : classObject.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(CommandSender.class) && Arrays.asList(declaredMethod.getParameterTypes()).contains(String[].class) ) {
                Command command = declaredMethod.getAnnotation(Command.class);
                if (this.getCommand(command.name().toLowerCase()) != null) {
                    return;
                }
                List<Method> list = this.commandClasses.getOrDefault(command.name().toLowerCase(), new LinkedList<>());
                list.add(declaredMethod);
                for (String alias : command.aliases()) {
                    this.commandClasses.put(alias.toLowerCase(), list);
                    this.invokers.put(alias.toLowerCase(), classObject);
                }
                this.commandClasses.put(command.name().toLowerCase(), list);
                this.invokers.put(command.name().toLowerCase(), classObject);
                this.commandInfos.add(new CommandInfo(command.name().toLowerCase(), command.description(), command.aliases(), command.description(), command.permissionMessage()));
            }
        }
    }


    public void unregisterCommand(Object command) {
        for (Method declaredMethod : command.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(CommandSender.class) && Arrays.asList(declaredMethod.getParameterTypes()).contains(String[].class) ) {
                Command cmd = declaredMethod.getAnnotation(Command.class);

                this.commandClasses.remove(cmd.name().toLowerCase());
                this.invokers.remove(cmd.name().toLowerCase());

                for (String alias : cmd.aliases()) {
                    this.commandClasses.remove(alias.toLowerCase());
                    this.invokers.remove(alias.toLowerCase());
                }
                this.commandInfos.remove(this.getCommand(cmd.name().toLowerCase()));
            }
        }
    }

    public boolean execute(CommandSender sender, boolean prefix, String line) {
        if (prefix) line = line.substring(1);

        String commandText = line.split(" ")[0];
        CommandInfo info = this.getCommand(commandText);
        if (info == null) {
            return false;
        }
        if (!sender.hasPermission(info.getPermission())) {
            sender.sendMessage(info.getPermissionMessage());
            return false;
        }
        String[] split = line.substring(commandText.length()).split(" ");
        List<String> args = new LinkedList<>();
        for (String argument : split) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                args.add(argument);
            }
        }
        try {
            this.commandClasses.forEach((command, methods) -> {
                if (command.equalsIgnoreCase(commandText)) {
                    methods.forEach(method -> {
                        try {
                            method.invoke(this.invokers.get(command), sender, args.toArray(new String[0]));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        } catch (ConcurrentModificationException e) {
            //Ignored because there is no error (but there is ? an exception)
        }
        return true;
    }

    public CommandInfo getCommand(String commandName) {
        for (CommandInfo commandInfo : this.commandInfos) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.<String>asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

}
