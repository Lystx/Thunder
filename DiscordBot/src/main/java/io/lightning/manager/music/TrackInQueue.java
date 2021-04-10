package io.lightning.manager.music;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter @AllArgsConstructor
public class TrackInQueue {
	
	private final TextChannel channel;
	private final String url;

}
