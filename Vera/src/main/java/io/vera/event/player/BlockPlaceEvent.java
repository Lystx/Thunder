
package io.vera.event.player;

import io.vera.server.world.Block;
import lombok.Getter;
import io.vera.entity.living.Player;
import io.vera.inventory.Item;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BlockPlaceEvent extends PlayerEvent{

    @Getter
    private final Block previous;
    @Getter
    private final Item itemToPlace;

    public BlockPlaceEvent(Player player, Block previous, Item place) {
        super(player);
        this.previous = previous;
        this.itemToPlace = place;
    }
}