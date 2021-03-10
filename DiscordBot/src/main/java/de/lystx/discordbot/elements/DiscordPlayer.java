package de.lystx.discordbot.elements;

import de.lystx.discordbot.Discord;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.function.Consumer;

@Getter @ToString
public class DiscordPlayer {

    private Member member;
    private final User user;

    public DiscordPlayer(User user) {
        this.user = user;

        this.member = Discord.getInstance().getInternal().getGuildById(Discord.getInstance().getConfigManager().getGuildID()).getMember(user);

        Discord.getInstance().getInternal().getGuilds().forEach(guild -> {
            if (guild.getId().equalsIgnoreCase(Discord.getInstance().getConfigManager().getGuildID())) {
                this.member = guild.getMember(user);
            }
        });
    }

    public boolean hasPermission(Permission permission) {
        if (this.member == null) {
            System.out.println("[DiscordBot] Member for " + user.getAsTag() + " wasn't found");
            return false;
        }
        return this.member.hasPermission(permission);
    }

    public boolean hasRoleFromID(String id) {
        return this.findRole(id, true) != null;
    }

    public boolean hasRole(String name) {
        String id = Discord.getInstance().getConfigManager().getID(name);
        return this.hasRoleFromID(id);
    }

    public void sendMessage(TextChannel textChannel, MessageEmbed messageEmbed) {
        textChannel.sendMessage(messageEmbed).queue();
    }

    public void sendMessage(MessageEmbed messageEmbed) {
        user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
            @Override
            public void accept(PrivateChannel privateChannel) {
                privateChannel.sendMessage(messageEmbed).queue();
            }
        });
    }

    public Role findRole(String id, boolean byID) {
        if (this.member == null) {
            System.out.println("[DiscordBot] Member for " + user.getAsTag() + " wasn't found");
            return null;
        }
        return member.getRoles().stream().filter(role -> byID ? role.getId().equalsIgnoreCase(id) : role.getName().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
