package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuteCommand extends CommandHandler {


    public MuteCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return Lightning.get().hasPermission(member);
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {
        final List<User> mentionedUsers = raw.getMentionedUsers();
        if (mentionedUsers.isEmpty()) {
            channel.sendMessage("Please provide at least one user").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
            return;
        }
        StringCreator stringCreator = new StringCreator();
        stringCreator.append("Muted following Member(s):");
        for (User mentionedUser : mentionedUsers) {
            stringCreator.append("> " + mentionedUser.getAsMention());


            Member member = guild.getMember(mentionedUser);

            if (member == null) {
                continue;
            }
            String id = Lightning.get().getConfigManager().getConfig(guild).getVson("roles").getString("muted");
            if (id.equalsIgnoreCase("roleID")) {
                channel.sendMessage("The Mute-Role is not defined yet!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                return;
            }
            Role muted = guild.getRoleById(id);
            if (muted == null) {
                channel.sendMessage("The Mute-Role does not exist (anymore ?) !").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                return;
            }
            guild.addRoleToMember(member, muted).queue();
        }

        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Lightning | Mute")
                        .setDescription(stringCreator.toString())
                        .setThumbnail("https://cdn3.iconfinder.com/data/icons/negative-parenting/229/negative-parenting-010-512.png")
                        .setColor(Color.GREEN)
                        .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();

        raw.delete().queue();
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
