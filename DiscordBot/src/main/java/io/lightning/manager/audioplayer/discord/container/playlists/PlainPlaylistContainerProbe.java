package io.lightning.manager.audioplayer.discord.container.playlists;

import io.lightning.manager.audioplayer.discord.container.MediaContainerDetectionResult;
import io.lightning.manager.audioplayer.discord.container.MediaContainerHints;
import io.lightning.manager.audioplayer.discord.container.MediaContainerProbe;
import io.lightning.manager.audioplayer.discord.tools.DataFormatTools;
import io.lightning.manager.audioplayer.discord.tools.io.SeekableInputStream;
import io.lightning.manager.audioplayer.discord.track.AudioReference;
import io.lightning.manager.audioplayer.discord.track.AudioTrack;
import io.lightning.manager.audioplayer.discord.track.AudioTrackInfo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.lightning.manager.audioplayer.discord.container.MediaContainerDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Probe for a playlist containing the raw link without any format.
 */
public class PlainPlaylistContainerProbe implements MediaContainerProbe {
  private static final Logger log = LoggerFactory.getLogger(PlainPlaylistContainerProbe.class);

  private static final Pattern linkPattern = Pattern.compile("^(?:https?|icy)://.*");

  @Override
  public String getName() {
    return "plain";
  }

  @Override
  public boolean matchesHints(MediaContainerHints hints) {
    return false;
  }

  @Override
  public MediaContainerDetectionResult probe(AudioReference reference, SeekableInputStream inputStream) throws IOException {
    if (!MediaContainerDetection.matchNextBytesAsRegex(inputStream, MediaContainerDetection.STREAM_SCAN_DISTANCE, linkPattern, StandardCharsets.UTF_8)) {
      return null;
    }

    log.debug("Track {} is a plain playlist file.", reference.identifier);
    return loadFromLines(DataFormatTools.streamToLines(inputStream, StandardCharsets.UTF_8));
  }

  private MediaContainerDetectionResult loadFromLines(String[] lines) {
    for (String line : lines) {
      Matcher matcher = linkPattern.matcher(line);

      if (matcher.matches()) {
        return MediaContainerDetectionResult.refer(this, new AudioReference(matcher.group(0), null));
      }
    }

    return MediaContainerDetectionResult.unsupportedFormat(this, "The playlist file contains no links.");
  }

  @Override
  public AudioTrack createTrack(String parameters, AudioTrackInfo trackInfo, SeekableInputStream inputStream) {
    throw new UnsupportedOperationException();
  }
}
