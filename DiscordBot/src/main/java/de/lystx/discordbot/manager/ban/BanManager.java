package de.lystx.discordbot.manager.ban;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.ChannelManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.function.Consumer;

public class BanManager extends ChannelManager {

    public void ban(DiscordPlayer player, String reason, String by) {
        Discord.getInstance().getGuild().addRoleToMember(player.getMember(), Discord.getInstance().getGuild().getRoleById(Discord.getInstance().getConfigManager().getID("Banned"))).queue();
        this.queueChannel(player, "ban-" + player.getUser().getId(), Discord.getInstance().getConfigManager().getBanCategory(), new Consumer<TextChannel>() {
            @Override
            public void accept(TextChannel textChannel) {
                textChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Valane | Du bist gebannt")
                        .setDescription("Dein akuteller Ban besteht dauerhaft\nGrund » " + reason + "\nVon » " + by + "\nIn diesem Channel kannst du gerne einen Entbannungsantrag stellen, wenn du das möchtest. Ansonsten kannst du auch einfach in diesem Channel verweilen.")
                        .setColor(Color.RED)
                        .build()).queue();
            }
        });
    }

    public void unban(DiscordPlayer player) {
        Discord.getInstance().getGuild().removeRoleFromMember(player.getMember(), Discord.getInstance().getGuild().getRoleById(Discord.getInstance().getConfigManager().getID("Banned"))).queue();
        this.deleteChannel("ban-" + player.getMember().getId());
    }

    public boolean isBanned(DiscordPlayer player) {
        return player.hasRole("Banned");
    }
}
