package io.lightning.manager.music;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.lightning.elements.commands.MusicCommand;
import io.lightning.manager.audioplayer.discord.player.AudioPlayer;
import io.lightning.manager.audioplayer.discord.player.event.AudioEventAdapter;
import io.lightning.manager.audioplayer.discord.track.AudioTrack;
import io.lightning.manager.audioplayer.discord.track.AudioTrackEndReason;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

@Getter @Setter
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private List<AudioTrack> queue;
    private final AtomicInteger currentpos;
    private boolean repeat;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedList<>();
        this.repeat = false;
        this.currentpos = new AtomicInteger(0);
        this.guild = guild;
    }
    
    public boolean isRepeat() {
    	return repeat;
    }
    
    public void setRepeat(boolean repeat) {
    	this.repeat = repeat;
    }

    public void queue(AudioTrack track) {
    	if (currentpos.get() >= queue.size()) {
    		currentpos.set(0);
    	}
    	queue.add(track);
    	player.startTrack(track, true);
    }

    public void nextTrack() {
        AudioTrack audioTrack;
    	if (currentpos.get() >= queue.size()) {
    		if (repeat) {
    			currentpos.set(0);
    			queue = queue.stream().map(AudioTrack::makeClone).collect(Collectors.toCollection(LinkedList::new));
    			player.startTrack(queue.get(0), false);
                audioTrack = queue.get(0);
    		} else {
    			player.startTrack(null, false);
    			this.queue.clear();
                PlayerManager.getInstance().getQueue().removeIf(track -> track.getChannel().getGuild().getIdLong() == guild.getIdLong());
    			PlayerManager.getInstance().getGuildMusicManager(guild).player.destroy();
    			AudioManager audioManager = guild.getAudioManager();
    			//TODO: CHECK IF LEAVING OR STAYING
    			//audioManager.closeAudioConnection();
                audioTrack = null;
    		}
    	} else {
    		queue.set(currentpos.get(), queue.get(currentpos.get()).makeClone());
    		player.startTrack(queue.get(currentpos.get()), false);
            audioTrack = queue.get(currentpos.get());;
    	}

        if (audioTrack == null) {
            return;
        }
        MusicCommand.CURRENT_DJ_INFO.editMessage(
            MusicCommand.format(audioTrack, MusicCommand.EXECUTOR, "0:00", "0:00", 1)
        ).queue();

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
        	currentpos.incrementAndGet();
            nextTrack();
        }
    }

    public int getCurrentPosition() {
    	return currentpos.get();
    }
    
    public List<AudioTrack> getQueueCopy() {
    	return new ArrayList<>(queue);
    }

}