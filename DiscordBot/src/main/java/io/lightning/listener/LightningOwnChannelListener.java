package io.lightning.listener;

import io.lightning.Lightning;
import io.vson.elements.object.VsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class LightningOwnChannelListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();


        final VsonObject customChannel = Lightning.get().getConfigManager().getConfig(guild).getVson("customChannel");
        String category = customChannel.getString("category");
        String name = customChannel.getString("name");



        if (!category.equalsIgnoreCase("null") && !name.equalsIgnoreCase("null")) {
            if (event.getChannelJoined().getName().equalsIgnoreCase(name)) {


                if (guild.getVoiceChannelsByName(member.getUser().getName() + "'s private Channel", true).size() == 0) {

                    VoiceChannel voiceChannel = guild.createVoiceChannel(category)
                            .setParent(guild.getCategoryById(category))
                            .setName(member.getUser().getName() + "'s private Channel")
                            .complete();
                    guild.moveVoiceMember(member, voiceChannel).queue();
                }

            }
        }
    }


    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();

        final VoiceChannel channelLeft = event.getChannelLeft();
        if (channelLeft.getName().endsWith("'s private Channel")) {
            if (channelLeft.getMembers().size() <= 0) {
                channelLeft.delete().queue();
            }
        }
    }
}
