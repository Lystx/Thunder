package io.lightning.listener;

import io.lightning.Lightning;
import io.lightning.utils.PlaceHolder;
import io.lightning.utils.Utils;
import io.vson.elements.object.VsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LightningMemberChangeListener extends ListenerAdapter {


    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();

        Lightning.get().getConfigManager().save(guild, member);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getMember().getRoles().isEmpty()) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(Lightning.get().getConfigManager().getConfig(event.getGuild()).getVson("roles").getString("default"))).queue();
        }

        final Guild guild = event.getGuild();

        final VsonObject load = Lightning.get().getConfigManager().load(guild, event.getMember());
        if (!load.getFile().exists()) {
            return;
        }
        for (String s : load.getList("roles", String.class)) {
            final Role roleById = guild.getRoleById(s);
            if (roleById == null) {
                return;
            }
            guild.addRoleToMember(event.getMember(), roleById).queue();
        }

        load.clear();
        load.save();

        String longId = Lightning.get().getConfigManager().getConfig(event.getGuild()).getVson("welcome").getString("channel");
        if (longId == null || longId.equalsIgnoreCase("null")) {
            return;
        }
        TextChannel textChannel = event.getGuild().getTextChannelById(longId);
        if (textChannel == null) {
            System.out.println("[Lightning] " + event.getGuild() + " couldn't handle joining of member " + event.getMember().getUser().getAsTag() + " because the WelcomeChannel does not exist!");
            return;
        }
        final VsonObject message = Lightning.get().getConfigManager().getConfig(event.getGuild()).getVson("welcome").getVson("message");
        PlaceHolder placeHolder = new PlaceHolder(event.getMember(), event.getGuild(), textChannel);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!message.getString("color").equalsIgnoreCase("null")) {
            embedBuilder.setColor(Utils.stringToColor(message.getString("color")));
        }
        if (message.getString("title") != null && !message.getString("title").equalsIgnoreCase("null")) {
            embedBuilder.setTitle(placeHolder.setContent(message.getString("title")).transform());
        }
        if (message.getString("thumbnail") != null && !message.getString("thumbnail").equalsIgnoreCase("null")) {
            embedBuilder.setThumbnail(placeHolder.setContent(message.getString("thumbnail")).transform());
        }
        if (message.getString("image") != null && !message.getString("image").equalsIgnoreCase("null")) {
            embedBuilder.setImage(placeHolder.setContent(message.getString("image")).transform());
        }

        VsonObject footer = message.getVson("footer");
        if (footer.getString("url") != null || footer.getString("url").equalsIgnoreCase("null")) {
            embedBuilder.setFooter(placeHolder.setContent(footer.getString("text")).transform(), placeHolder.setContent(footer.getString("url")).transform());
        } else if (footer.getString("text") != null && !footer.getString("text").equalsIgnoreCase("null")) {
            embedBuilder.setFooter(placeHolder.setContent(footer.getString("text")).transform());
        }
        if (!message.getString("content").trim().isEmpty()) {
            embedBuilder.setDescription(placeHolder.setContent(message.getString("content")).transform());
        }
        textChannel.sendMessage(embedBuilder.build()).queue();

    }




    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        final Member member = event.getMember();
        String id = Lightning.get().getConfigManager().getConfig(event.getGuild()).getVson("roles").getString("muted");
        Role muted = event.getGuild().getRoleById(id);
        if (member.getRoles().contains(muted)) {
           // member.deafen(true).queue();
            member.mute(true).queue();
        }
    }
}
