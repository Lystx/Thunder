
package io.vera.event.player;

import io.vera.server.world.Block;
import lombok.Getter;
import lombok.Setter;
import io.vera.entity.living.Player;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PlayerDigEvent extends PlayerEvent {

    @Getter
    private final Block block;


    public PlayerDigEvent(Player player, Block block) {
        super(player);
        this.block = block;
    }
}