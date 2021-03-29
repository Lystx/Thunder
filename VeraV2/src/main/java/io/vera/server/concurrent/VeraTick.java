package io.vera.server.concurrent;

import io.vera.logger.Logger;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.World;
import io.vera.server.world.WorldLoader;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class VeraTick extends Thread {
  private static final long TICK_MILLIS = TimeUnit.SECONDS.toMillis(1L) / 20L;
  
  private final Logger logger;
  
  public VeraTick(Logger logger) {
    super("VRA - Tick");
    this.logger = logger;
  }
  
  public void run() {
    try {
      while (true) {
        long start = System.currentTimeMillis();
        for (World world : WorldLoader.getInstance().worlds())
          world.tick(); 
        for (VeraPlayer player : VeraPlayer.getPlayers().values())
          player.tick(); 
        VeraScheduler.getInstance().tick();
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        long waitTime = TICK_MILLIS - elapsed;
        if (waitTime < 0L) {
          this.logger.debug("Server running behind " + -waitTime + "ms, skipped " + (-waitTime / TICK_MILLIS) + " ticks");
          continue;
        } 
        Thread.sleep(waitTime);
      } 
    } catch (InterruptedException e) {
    
    } catch (Exception e) {}
  }
}
