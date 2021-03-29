package io.vera.event.player;

import io.vera.entity.living.Player;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PlayerInteractEvent extends PlayerEvent {

    public PlayerInteractEvent(Player player) {
        super(player);
    }
}
