package io.lightning.manager.music;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.lightning.manager.audioplayer.discord.player.AudioLoadResultHandler;
import io.lightning.manager.audioplayer.discord.player.AudioPlayerManager;
import io.lightning.manager.audioplayer.discord.player.DefaultAudioPlayerManager;
import io.lightning.manager.audioplayer.discord.source.AudioSourceManagers;
import io.lightning.manager.audioplayer.discord.tools.FriendlyException;
import io.lightning.manager.audioplayer.discord.track.AudioPlaylist;
import io.lightning.manager.audioplayer.discord.track.AudioTrack;

import io.lightning.Lightning;
import io.lightning.utils.StringCreator;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

@Getter
public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;    
    private final ConcurrentLinkedQueue<TrackInQueue> queue;
    private final AtomicLong lastqueue;
    private final PlayerManager runnableINSTANCE;
    private final Timer runnable;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        
        this.queue = new ConcurrentLinkedQueue<>();
        this.lastqueue = new AtomicLong(0);
        this.runnableINSTANCE = this;
        this.runnable = run();
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        queue.add(new TrackInQueue(channel, trackUrl));
    }
    
    private Timer run() {
    	Timer timer = new Timer();
    	timer.schedule(new TimerTask() {
    		public void run() {


    			new Thread(() -> {

					if (!runnableINSTANCE.equals(getInstance())) {
						runnable.cancel();
						return;
					}

					if (lastqueue.get() < (System.currentTimeMillis() - 1000)) {

						TrackInQueue tiq = queue.poll();

						if (tiq != null) {

							TextChannel channel = tiq.getChannel();
							String trackUrl = tiq.getUrl();
							GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

							playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
								@Override
								public void trackLoaded(AudioTrack track) {
									channel.sendMessage("Adding to queue " + track.getInfo().title + (queue.isEmpty() ? "" : " (" + queue.size() + " left in loading queue)")).queue();
									play(musicManager, track);
									lastqueue.set(System.currentTimeMillis());
								}

								@Override
								public void playlistLoaded(AudioPlaylist playlist) {
									AudioTrack selectedTrack = playlist.getSelectedTrack();
									Lightning.get().setPlayListLink(tiq.getUrl());

									if (selectedTrack == null) {
										selectedTrack = playlist.getTracks().get(0);
									}

									boolean doAdd = false;
									AtomicInteger left = new AtomicInteger(playlist.getTracks().size());

									StringCreator sb = new StringCreator();
									sb.append("Name: " + playlist.getName());
									sb.append("Loaded: " + 0 + "/" + playlist.getTracks().size());

									final Message complete = channel.sendMessage(
											new EmbedBuilder()
													.setTitle("PlayList | Queue")
													.setDescription(sb.toString())
													.setThumbnail("https://upload.wikimedia.org/wikipedia/commons/b/b9/Youtube_loading_symbol_1_(wobbly).gif")
													.setFooter("Track : None")
													.build()

									).complete();

									for (AudioTrack track : playlist.getTracks()) {

										int remaining = left.decrementAndGet();
										sb.clear();
										sb.append("Name: " + playlist.getName());
										sb.append("Loaded: " + (playlist.getTracks().size() - remaining) + "/" + playlist.getTracks().size());
										if (!doAdd && track.equals(selectedTrack)) {
											doAdd = true;
										}
										if (doAdd) {
											play(musicManager, track);
										}
										lastqueue.set(System.currentTimeMillis());
										try {
											TimeUnit.SECONDS.sleep(1);
										} catch (InterruptedException e) {
											//IGNORE
										}
										try {
											complete.editMessage(
													new EmbedBuilder()
															.setTitle("PlayList | Queue")
															.setDescription(sb.toString())
															.setColor(Color.ORANGE)
															.setThumbnail("https://upload.wikimedia.org/wikipedia/commons/b/b9/Youtube_loading_symbol_1_(wobbly).gif")
															.setFooter("Track : " + track.getInfo().title, "https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/maxresdefault.jpg")
															.build()
											).complete(false);
										} catch (RateLimitedException e) {

										}

										if (remaining <= 1) {

											try {
												complete.editMessage(
														new EmbedBuilder()
																.setTitle("PlayList | Queue")
																.setDescription("Playlist loaded successfully!")
																.setColor(Color.GREEN)
																.setThumbnail("https://www.freeiconspng.com/uploads/success-icon-19.png")
																.build()
												).complete(false);
											} catch (RateLimitedException e) {

											}
										}
									}
								}

								@Override
								public void noMatches() {
									channel.sendMessage("Nothing found by " + trackUrl).queue();
								}

								@Override
								public void loadFailed(FriendlyException e) {
									channel.sendMessage("Could not play: " + e.getMessage()).queue();
								}
							});
						}
					}
				}).start();

    		}
    	}, 0, 1);
    	return timer;
    }
    
    private void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}