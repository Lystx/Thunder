package de.lystx.discordbot.manager;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.function.Consumer;

public abstract class ChannelManager {

    public void queueChannel(DiscordPlayer player, String name, String category, Consumer<TextChannel> consumer) {

        Discord.getInstance().getGuild().createTextChannel(name, Discord.getInstance().getGuild().getCategoryById(category)).queue(textChannel -> {
            textChannel.createPermissionOverride(player.getMember()).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_EXT_EMOJI,
                    Permission.MESSAGE_ADD_REACTION
            ).queue();
            consumer.accept(textChannel);
        });
    }

    public boolean hasChannel(String name) {
        for (TextChannel textChannel : Discord.getInstance().getGuild().getTextChannelsByName(name, true)) {
            if (textChannel.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void deleteChannel(String name) {
        TextChannel textChannel = Discord.getInstance().getGuild().getTextChannelById(name);
        if (textChannel == null) {
            System.out.println("[DiscordBot] TextChannel for ChannelManager is null");
            return;
        }
        textChannel.delete().queue();
    }
}
