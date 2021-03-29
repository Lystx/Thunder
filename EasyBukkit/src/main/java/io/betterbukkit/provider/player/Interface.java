package io.betterbukkit.provider.player;

import io.betterbukkit.provider.item.Item;

public interface Interface {

    String getTitle();

    void close();

    void setItem(int slot, Item item);

    void addItem(Item item);

    Item getItem(int slot);

    int getSlot(Item item);

    void open(Player player);
}
