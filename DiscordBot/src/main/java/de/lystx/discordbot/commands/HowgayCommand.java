package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HowgayCommand{

    @Command(name = "howgay", description = "Shows you how gay you are")
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {
        int gay = ThreadLocalRandom.current().nextInt(0, 100);
        String name;
        if (args.length >= 1) {
            final List<User> mentionedUsers = message.getMentionedUsers();
            if (!mentionedUsers.isEmpty()) {
                if (mentionedUsers.get(0).getAsTag().equalsIgnoreCase("Valane#8410")) {
                    Discord.getInstance().soNicht(player, textChannel);
                    return;
                }
                name = mentionedUsers.get(0).getAsMention();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (String arg : args) {
                    stringBuilder.append(arg).append(" ");
                }
                name = stringBuilder.toString();
            }
        } else {
            name = player.getMember().getAsMention();
        }
        textChannel.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setTitle("Gay Calculating Machine").setDescription(name + " ist " + gay + "% :gay_pride_flag:").build()).queue();

    }
}
