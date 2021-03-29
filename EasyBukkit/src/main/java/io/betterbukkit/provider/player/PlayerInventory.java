package io.betterbukkit.provider.player;

import io.betterbukkit.provider.item.Item;
import io.betterbukkit.provider.item.Substance;

public interface PlayerInventory {

    void setArmor(ArmorSlot slot, Item item);

    void setArmor(Substance substance);

    void clear();

    void setItem(int slot, Item item);

    Item getArmor(ArmorSlot slot);

    enum ArmorSlot {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;
    }
}
