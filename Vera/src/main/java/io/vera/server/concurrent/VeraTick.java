
package io.vera.server.concurrent;

import io.vera.server.world.World;
import io.vera.server.world.WorldLoader;
import io.vera.logger.Logger;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.TimeUnit;


@Immutable
public final class VeraTick extends Thread {
    private static final long TICK_MILLIS = TimeUnit.SECONDS.toMillis(1) / 20;
    private final Logger logger;

    public VeraTick(Logger logger) {
        super("VRA - Tick");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long start = System.currentTimeMillis();

                for (World world : WorldLoader.getInstance().worlds()) {
                    world.tick();
                }

                for (VeraPlayer player : VeraPlayer.getPlayers().values()) {
                    player.tick();
                }

                VeraScheduler.getInstance().tick();

                long end = System.currentTimeMillis();
                long elapsed = end - start;
                long waitTime = TICK_MILLIS - elapsed;
                if (waitTime < 0) {
                    this.logger.debug("Server running behind " +
                            -waitTime + "ms, skipped " + (-waitTime / TICK_MILLIS) + " ticks");
                } else {
                    Thread.sleep(waitTime);
                }
            } catch (Exception e) {
                break;
            }
        }
    }
}