package io.betterbukkit.provider.world;

import io.betterbukkit.provider.item.Item;
import io.betterbukkit.provider.player.Player;

import java.util.List;

public interface World {

    String getName();

    Block getBlockAt(int var1, int var2, int var3);

    Block getBlockAt(Position var1);

    List<Player> getPlayers();

    void dropItem(Item item, Position position);

}
