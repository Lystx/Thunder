package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserInfoCommand {

    @Command(name = "userInfo", aliases = {"info", "user"}, description = "Shows infos about a User")
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {

        User user;
        Member member;
        if (args.length == 1) {
            List<User> mentionedUsers = message.getMentionedUsers();
            user = mentionedUsers.get(0);
            member = Discord.getInstance().getMember(user.getAsTag());
        } else {
            user = player.getUser();
            member = player.getMember();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy - hh:mm:ss");
        textChannel.sendMessage(new EmbedBuilder()
                .setColor(member.getColor())
                .setTitle(user.getName() + " | Information", user.getEffectiveAvatarUrl())
                .setThumbnail("https://www.seekpng.com/png/full/368-3685399_lucky-blocks-super-mario-question-block.png")
                .setDescription("Beigetreten " + member.getTimeJoined().format(formatter))
                .addField("Erstellt", member.getTimeCreated().format(formatter), true)
                .addField("Status", member.getOnlineStatus().name(), true)
                .addField("Gebannt", new DiscordPlayer(user).hasRole("Banned") ? "Ja" : "Nein", true)
                .addField("Gemutet", new DiscordPlayer(user).hasRole("Muted") ? "Ja" : "Nein", true)
                .addField("Spielt", member.getActivities().isEmpty() ? "Nichts" : member.getActivities().get(0).getName(), true)
                .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                .build()).queue();

    }

}
