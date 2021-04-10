package io.lightning.listener;

import io.lightning.manager.audioplayer.discord.player.AudioPlayer;
import io.lightning.Lightning;
import io.lightning.elements.commands.MusicCommand;
import io.lightning.manager.music.PlayerManager;
import io.lightning.manager.music.TrackScheduler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LightningMusicListener extends ListenerAdapter {


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT)) {
            return;
        }

        Guild guild = event.getGuild();
        Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
        MessageChannel channel = event.getChannel();
        MessageReaction.ReactionEmote reaction = event.getReactionEmote();
        if (!reaction.isEmoji()) {
            return;
        }

        String emote = reaction.getEmoji();

        if (!message.getAuthor().equals(Lightning.get().getJda().getSelfUser())) {
            return;
        }

        if (event.getUser().equals(Lightning.get().getJda().getSelfUser())) {
            return;
        }


        StringCreator creator = new StringCreator();
        String content;

        if (!MusicCommand.MESSAGES_TO_DELETE.contains(message)) {
            return;
        }



        for (MessageEmbed embed : message.getEmbeds()) {
            creator.append(embed.getDescription());
        }
        content = creator.toString();
        MessageEmbed embed = message.getEmbeds().get(0);

        if (content != null && embed.getTitle().startsWith("PlayList | [") && embed.getTitle().endsWith("]")) {

            switch (emote) {

                case "➡":
                    int currentPage = MusicCommand.CURRENT_PAGE;
                    currentPage++;
                    MusicCommand.CURRENT_PAGE = currentPage;
                    message.removeReaction("➡", event.getUser()).queue();
                    EmbedBuilder messageEmbed = MusicCommand.EMBED_MESSAGES.get(currentPage);
                    if (messageEmbed == null) {
                        return;
                    }
                    message.editMessage(messageEmbed.build()).queue();
                    break;
                case "⬅":
                    int currentPage2 = MusicCommand.CURRENT_PAGE;
                    currentPage2--;
                    MusicCommand.CURRENT_PAGE = currentPage2;
                    message.removeReaction("⬅", event.getUser()).queue();
                    EmbedBuilder messageEmbed2 = MusicCommand.EMBED_MESSAGES.get(currentPage2);
                    if (messageEmbed2 == null) {
                        return;
                    }
                    message.editMessage(messageEmbed2.build()).queue();
                    break;
            }
        } else {
            switch (emote) {
                case "⏸️": {
                    PlayerManager manager = PlayerManager.getInstance();
                    AudioPlayer player = manager.getGuildMusicManager(guild).player;
                    player.setPaused(!player.isPaused());
                    channel.sendMessage(player.isPaused() ? "Paused the music player!" : "Resumed the music player!").queue(message1 -> message1.delete().queueAfter(1, TimeUnit.SECONDS));
                    break;
                }
                case "⏭️": {
                    PlayerManager manager = PlayerManager.getInstance();
                    TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                    scheduler.getCurrentpos().incrementAndGet();
                    scheduler.nextTrack();
                    channel.sendMessage("Skipping to the next track!").queue(message1 -> message1.delete().queueAfter(1, TimeUnit.SECONDS));
                    break;
                }
                case "⏮️": {
                    PlayerManager manager = PlayerManager.getInstance();
                    TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                    scheduler.getCurrentpos().decrementAndGet();
                    scheduler.nextTrack();
                    channel.sendMessage("Going back to the previous track!").queue(message1 -> message1.delete().queueAfter(1, TimeUnit.SECONDS));
                    break;
                }
                case "🔀": {
                    PlayerManager manager = PlayerManager.getInstance();
                    TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                    Collections.shuffle(scheduler.getQueue());
                    channel.sendMessage("Randomized the position of tracks in the playlist!").queue(message1 -> message1.delete().queueAfter(1, TimeUnit.SECONDS));
                    break;
                }
                case "\uD83D\uDD01": {
                    TrackScheduler scheduler = PlayerManager.getInstance().getGuildMusicManager(guild).scheduler;
                    boolean repeat = !scheduler.isRepeat();
                    scheduler.setRepeat(repeat);
                    channel.sendMessage("Repeating mode is now turned " + (repeat ? "**ON**" : "**OFF**")).queue(message1 -> message1.delete().queueAfter(1, TimeUnit.SECONDS));
                    break;
                }
                case "🛑": {
                    AudioManager audioManager = event.getGuild().getAudioManager();

                    if (!audioManager.isConnected()) {
                        channel.sendMessage("I'm not connected to a voice channel").queue();
                        return;
                    }

                    VoiceChannel voiceChannel = audioManager.getConnectedChannel();

                    if (!voiceChannel.getMembers().contains(event.getMember())) {
                        channel.sendMessage("You have to be in the same voice channel as me to use this").queue();
                        return;
                    }
                    PlayerManager manager = PlayerManager.getInstance();
                    manager.getGuildMusicManager(guild).player.destroy();
                    manager.getQueue().removeIf(track -> track.getChannel().getGuild().getIdLong() == guild.getIdLong());
                    manager.getGuildMusicManager(guild).scheduler.getQueue().clear();
                    audioManager.closeAudioConnection();
                    channel.sendMessage("Disconnected from your channel").queue();
                    message.removeReaction("⏸️", Lightning.get().getJda().getSelfUser()).queue();
                    message.removeReaction("⏭️", Lightning.get().getJda().getSelfUser()).queue();
                    message.removeReaction("🛑", Lightning.get().getJda().getSelfUser()).queue();
                    message.removeReaction("⏮️", Lightning.get().getJda().getSelfUser()).queue();
                    message.removeReaction("🔀", Lightning.get().getJda().getSelfUser()).queue();
                    message.removeReaction("❌", Lightning.get().getJda().getSelfUser()).queue();
                    break;
                }
            }
            if (message == null || emote == null || event.getUser() == null) {
                return;
            }
            try {

                message.removeReaction(emote, event.getUser()).queue();
            } catch (Exception e) {
                //IGNORING IT
            }
        }

        if (emote.equals("❌")) {
            message.delete().queue();
        }
    }

}
