package io.vera.event.player;

import io.vera.entity.living.Player;
import io.vera.server.world.Block;
import lombok.Getter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe @Getter
public class PlayerDigEvent extends PlayerEvent {

    private final Block block;

    public PlayerDigEvent(Player player, Block block) {
        super(player);
        this.block = block;
    }
}
