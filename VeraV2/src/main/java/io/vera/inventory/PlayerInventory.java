package io.vera.inventory;

import javax.annotation.Nonnull;

public interface PlayerInventory extends Inventory {

    Item getHeldItem();

    Item getOffHeldItem();

    int getSelectedSlot();
}
