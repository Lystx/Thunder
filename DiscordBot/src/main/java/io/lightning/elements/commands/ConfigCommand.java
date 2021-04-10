package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfigCommand extends CommandHandler {

    public static List<String> CONFIG_LOADERS = new LinkedList<>();

    public ConfigCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return Lightning.get().hasPermission(member);
    }


    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("load")) {
                final VsonObject config = Lightning.get().getConfigManager().getConfig(guild);
                StringCreator creator = new StringCreator();
                creator.append("```json");
                creator.append(config.toString(FileFormat.JSON));
                creator.append("```").append("");
                creator.append("Just copy the config and edit it then type **" + Lightning.get().getCommandManager(guild).getPrefix() + "config save** !");
                channel.sendMessage(
                        new EmbedBuilder()
                                 .setTitle("Lightning | Config")
                                .setDescription(creator.toString())
                                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                        .build()
                ).queue();
            } else if (args[0].equalsIgnoreCase("save")) {
                if (CONFIG_LOADERS.contains(executor.getId())) {
                    channel.sendMessage("You already executed this command!").queue();
                    return;
                }
                CONFIG_LOADERS.add(executor.getId());
                channel.sendMessage("Now paste the config in here! You got 10 Seconds to do so!").queue();
                Lightning.get().schedule(() -> CONFIG_LOADERS.remove(executor.getId()), 10, TimeUnit.SECONDS);
            } else {
                syntax("", channel, executor.getUser());
            }
        } else {
            syntax("", channel, executor.getUser());
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {
        String p = Lightning.get().getCommandManager(channel.getGuild()).getPrefix();
        StringCreator creator = new StringCreator();
        creator.append(p + "config load | Prints you the config");
        creator.append(p + "config save | Makes you save the config");
        channel.sendMessage(
                new EmbedBuilder()
                .setTitle("Lightning | Config")
                .setDescription(creator.toString())
                .setFooter("Executor | " + executor.getAsTag(), executor.getEffectiveAvatarUrl())
                .build()
        ).queue();
    }
}
