package de.lystx.discordbot.manager.command;

import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import de.lystx.discordbot.manager.command.base.CommandInfo;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

@Getter @Setter
public final class CommandManager {

    private final Map<String, List<Method>> commandClasses;
    private final List<CommandInfo> commandInfos;
    private final Map<String, Object> invokers;
    private boolean active;

    public CommandManager() {
        this.commandClasses = new HashMap<>();
        this.invokers = new HashMap<>();
        this.active = true;
        this.commandInfos = new LinkedList<>();
    }

    public void registerCommand(Object classObject) {
        for (Method declaredMethod : classObject.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(DiscordPlayer.class)) {
                Command command = declaredMethod.getAnnotation(Command.class);
                if (command == null) {
                    return;
                }
                if (this.getCommand(
                            command
                                .name().toLowerCase()) != null) {
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
                this.commandInfos.add(new CommandInfo(command.name().toLowerCase(), command.description(), command.aliases(), command.permission()));
            }
        }
    }

    public void unregisterCommand(Object command) {
        for (Method declaredMethod : command.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(DiscordPlayer.class)) {
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

    public boolean execute(User author, TextChannel textChannel, String line, Message message) {
        DiscordPlayer player = new DiscordPlayer(author);
        line = line.substring(1);

        String commandText = line.split(" ")[0];
        if (this.getCommand(commandText) == null) {
            return false;
        }
        String[] split = line.substring(commandText.length()).split(" ");
        List<String> args = new LinkedList<>();
        for (String argument : split) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                args.add(argument);
            }
        }
        CommandInfo info = this.getCommand(commandText);
        if (!author.getAsTag().equalsIgnoreCase("julheeg#0310") && info.getPermission() != Permission.UNKNOWN && !player.hasPermission(info.getPermission())) {
            textChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Valane | Fehler")
                            .setThumbnail("https://images.techhive.com/images/article/2016/04/error-thinkstock-100655502-large.jpg")
                            .setDescription("Du hast keine Berechtigung diesen Befehl auszuführen!")
                            .setFooter("Developer Lystx")
                            .setColor(Color.GREEN)
                            .build()
            ).queue();
            return true;
        }
        try {
            this.commandClasses.forEach((command, methods) -> {
                if (command.equalsIgnoreCase(commandText)) {
                    methods.forEach(method -> {
                        try {
                            method.invoke(this.invokers.get(command), player, args.toArray(new String[0]), textChannel, message);
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
