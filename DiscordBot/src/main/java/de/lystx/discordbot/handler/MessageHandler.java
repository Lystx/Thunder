package de.lystx.discordbot.handler;

import de.lystx.discordbot.Discord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MessageHandler extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            if (event.getMessage().getContentRaw().startsWith(Discord.getInstance().getConfigManager().getCommandPrefix())) {
                if (!Discord.getInstance().getCommandManager().execute(event.getMessage().getAuthor(), event.getTextChannel(), event.getMessage().getContentRaw(), event.getMessage())) {
                    event.getTextChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription("This command does not exist!").build()).queue();
                }
            }
        }
    }
}
