package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UnmuteCommand extends CommandHandler {


    public UnmuteCommand(String name, String description, CommandCategory category, String... aliases) {
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
        stringCreator.append("Unmuted following Member(s):");
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
            if (!member.getRoles().contains(muted)) {
                channel.sendMessage(member.getUser().getAsMention() + " is not muted!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                return;
            }
            guild.removeRoleFromMember(member, muted).queue();
        }

        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Lightning | Unmute")
                        .setDescription(stringCreator.toString())
                        .setThumbnail("https://cdn0.iconfinder.com/data/icons/octicons/1024/law-512.png")
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
