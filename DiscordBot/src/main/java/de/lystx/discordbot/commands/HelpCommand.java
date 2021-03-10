package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import de.lystx.discordbot.manager.command.base.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class HelpCommand {

    @Command(name = "help", aliases = {"?", "hilfe"}, description = "Manages Admin command", permission = Permission.ADMINISTRATOR)
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CommandInfo commandInfo : Discord.getInstance().getCommandManager().getCommandInfos()) {
            if (commandInfo.getPermission() == Permission.UNKNOWN || player.hasPermission(commandInfo.getPermission())) {
                stringBuilder.append(commandInfo.getName()).append(" | ").append(commandInfo.getDescription()).append("\n");
            }
        }
        textChannel.sendMessage(new EmbedBuilder()
                .setTitle("Valane | Hilfe")
                .setColor(Color.YELLOW)
                .setThumbnail("https://i.pinimg.com/564x/fe/d4/19/fed419c2f545c29abced3ea21c70cdd6.jpg")
                .setDescription(stringBuilder.toString())
                .setFooter("Executor " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                .build()).queue();
    }

}
