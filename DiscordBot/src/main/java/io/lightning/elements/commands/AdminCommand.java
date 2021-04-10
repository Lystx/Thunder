package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdminCommand extends CommandHandler {

    Guild guild;

    public AdminCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getId().equalsIgnoreCase("813305613671989259");
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("shutdown")) {
                StringCreator stringCreator = new StringCreator();
                stringCreator.append("Guilds shutting down...");
                for (Guild guild1 : Lightning.get().getJda().getGuilds()) {
                    stringCreator.append(guild1.getName() + " (" + guild1.getId() + ")");
                }

                channel.sendMessage(
                        new EmbedBuilder()
                                .setTitle("Lightning | Shut down")
                                .setColor(Color.BLACK)
                                .setThumbnail("https://www.iconexperience.com/_img/v_collection_png/256x256/shadow/sign_stop.png")
                                .setDescription(stringCreator.toString())
                                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                                .build()
                ).queue(message -> Lightning.get().shutdown());
            } else if (args[0].equalsIgnoreCase("list")) {
                StringCreator stringCreator = new StringCreator();
                stringCreator.append("Guilds Lightning is running on:");
                for (Guild guild1 : Lightning.get().getJda().getGuilds()) {
                    stringCreator.append(" > " + guild1.getName() + " (" + guild1.getId() + ")");
                }
                channel.sendMessage(
                        new EmbedBuilder()
                                .setTitle("Lightning | Guilds")
                                .setColor(Color.YELLOW)
                                .setDescription(stringCreator.toString())
                                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                                .build()
                ).queue();
            } else {
                syntax("", channel, executor.getUser());
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String id = args[1];
                Guild get = Lightning.get().getJda().getGuildById(id);
                if (get == null) {
                    channel.sendMessage("There is no guild with the ID " + id + " that contains LightningBot!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    return;
                }

                List<Member> online = new LinkedList<>(guild.getMembers());
                online.removeIf(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE));

                channel.sendMessage(
                        new EmbedBuilder()
                            .setThumbnail(guild.getIconUrl())
                            .setTitle("Lightning | Guild Info")
                                .addField("Name", guild.getName(), true)
                                .addField("Description", get.getDescription() == null ? "No Description" : guild.getDescription(), true)
                                .addField("Online", online.size() + "/" + get.getMembers().size(), true)
                                .addField("Owner", get.getOwner().getUser().getAsMention() ,true)
                                .addField("Region", get.getRegion().getName() ,true)
                                .addField("Boosters", get.getBoosters().size() + "",true)
                                .addField("BoostTier", Guild.BoostTier.fromKey(get.getBoostTier().getKey()).name(),true)
                                .addField("Emotes", get.getEmotes().size() + "",true)
                                .addField("SplashURL", get.getSplashUrl() == null ? "No SplashURL" : get.getSplashUrl(),true)
                                .setImage(get.getBannerUrl())
                        .build()
                ).queue();
            } else {
                syntax("", channel, executor.getUser());
            }
        } else {
            syntax("", channel, executor.getUser());
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {
        StringCreator creator = new StringCreator();
        String p = Lightning.get().getCommandManager(guild).getPrefix();
        creator.append(p + "admin shutdown | Shuts down Lightning");
        creator.append(p + "admin list | Lists all Guilds");
        creator.append(p + "admin info <id> | Gives infos on a Guild");

        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Lightning | Administration")
                        .setDescription(creator.toString())
                        .setColor(new Color(146, 0, 0))
                        .setThumbnail("https://camo.githubusercontent.com/dee371eb747eca908a562cd37334ab9257fad5a456dd34b46596185b119b285a/68747470733a2f2f63646e2e6f6e6c696e65776562666f6e74732e636f6d2f7376672f696d675f3332353738382e706e67")
                        .setFooter("Executor | " + executor.getAsTag(), executor.getEffectiveAvatarUrl())
                .build()
        ).queue();
    }
}
