package io.vera.event.player;

import io.vera.entity.living.Player;
import io.vera.inventory.Item;
import io.vera.server.world.Block;
import lombok.Getter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe @Getter
public class BlockPlaceEvent extends PlayerEvent {

    private final Block previous;
    private final Item itemToPlace;

    public BlockPlaceEvent(Player player, Block previous, Item place) {
        super(player);
        this.previous = previous;
        this.itemToPlace = place;
    }
}
