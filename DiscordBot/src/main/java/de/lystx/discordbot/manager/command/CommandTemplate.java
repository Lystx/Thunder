package de.lystx.discordbot.manager.command;

import de.lystx.discordbot.elements.DiscordPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface CommandTemplate {

    void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message);
}
