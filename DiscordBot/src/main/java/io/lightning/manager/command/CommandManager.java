package io.lightning.manager.command;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

@Getter @Setter
public class CommandManager {

    private final List<CommandHandler> commands;
    private String prefix;
    private boolean active;
    private final Guild guild;

    public CommandManager(String prefix, Guild guild) {
        this.prefix = prefix;
        this.guild = guild;
        this.active = true;
        this.commands = new LinkedList<>();
    }

    public void registerCommand(CommandHandler commandHandler) {
        this.commands.add(commandHandler);
    }

    public boolean execute(boolean prefix, String line, Guild guild, TextChannel channel, User user, Message message) {

        if (prefix) line = line.substring(1);
        String commandText = line.split(" ")[0];
        final CommandHandler cmd = this.getCommand(commandText);
        if (cmd == null) {
            return false;
        }

        String[] split = line.substring(commandText.length()).split(" ");
        List<String> args = new LinkedList<>();
        for (String argument : split) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                args.add(argument);
            }
        }
        split = args.toArray(new String[0]);

        Member member = guild.getMember(user);
        if (member == null) {
            System.out.println("[Lightning] Couldn't get Member for User " + user.getAsTag() + "!");
            return true;
        }
        if (!cmd.hasPermission(member)) {
            channel.sendMessage(new EmbedBuilder().setTitle("Lightning | Manager").setDescription("You are not permitted to perform this command!").setFooter("Lacked by | " + user.getAsTag(), user.getEffectiveAvatarUrl()).build()).queue();
            return true;
        }
        cmd.execute(split, message, member, channel, guild);
        return true;
    }

    public List<CommandHandler> getCommands(CommandCategory category) {
        List<CommandHandler> list = new LinkedList<>();
        for (CommandHandler command : commands) {
            if (command.getCategory().equals(category)) {
                list.add(command);
            }
        }
        return list;
    }

    public CommandHandler getCommand(String commandName) {
        for (CommandHandler commandInfo : this.commands) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

}
