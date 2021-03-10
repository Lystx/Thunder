package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class StopCommand {

    @Command(name = "stop", aliases = {"shutdown", "exit"}, description = "Shuts the DiscordBot down!", permission = Permission.ADMINISTRATOR)
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {

        textChannel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Valane | Commands")
                        .setThumbnail("https://upload.wikimedia.org/wikipedia/commons/0/0c/Crystal_Clear_action_exit.png")
                        .setDescription("Shutting down DiscordBot...")
                        .setFooter("Developer Lystx | Executor " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                        .setColor(Color.GREEN)
                        .build()
        ).queue();
        Discord.getInstance().shutdown();
    }
}
