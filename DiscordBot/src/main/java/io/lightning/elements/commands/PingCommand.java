package io.lightning.elements.commands;

import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;


public class PingCommand extends CommandHandler {

    public PingCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {

        long time = System.currentTimeMillis();
        channel.sendMessage(new EmbedBuilder()
                .setTitle("Lightning | Ping")
                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                .setThumbnail("https://cdn2.iconfinder.com/data/icons/color-ui-kit/48/connectivity-512.png")
                .setColor(Color.WHITE)
                .setDescription("Calculating Ping...")
                .build())
                .queue(response -> {
                    response.editMessage(
                            new EmbedBuilder()
                                    .setDescription("Ping: " + (System.currentTimeMillis() - time) + "ms" )
                                    .setTitle("Lightning | Ping")
                                    .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                                    .setThumbnail("https://cdn2.iconfinder.com/data/icons/color-ui-kit/48/connectivity-512.png")
                                    .setColor(Color.GREEN)
                                    .build()).queue();
        });
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
