package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import de.lystx.discordbot.manager.command.base.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PingCommand {

    @Command(name = "ping", aliases = {"ms", "pong"}, description = "Shows ping")
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {
        textChannel.sendMessage(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Valane | Ping")
                .setDescription("Bot: " + Discord.getInstance().getInternal().getGatewayPing() + "ms\n" +
                        "Nachricht: " + message.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS) + "ms")
                .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                .build()).queue();

    }

}
