
package io.vera.inventory;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum InventoryType {
    PLAYER("Player"),
    CONTAINER,
    CHEST,
    CRAFTING_TABLE,
    FURNACE,
    DISPENSER,
    ENCHANTING_TABLE,
    BREWING_STAND,
    VILLAGER,
    BEACON,
    ANVIL,
    HOPPER,
    DROPPER,
    SHULKER_BOX,
    HORSE("EntityHorse");

    private final String name;

    InventoryType() {
        this.name = "minecraft:" + this.name().toLowerCase();
    }

    InventoryType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}