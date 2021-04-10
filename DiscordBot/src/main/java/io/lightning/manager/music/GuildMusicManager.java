package io.lightning.manager.music;

import io.lightning.manager.audioplayer.discord.player.AudioPlayer;
import io.lightning.manager.audioplayer.discord.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;


public class GuildMusicManager {

    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}