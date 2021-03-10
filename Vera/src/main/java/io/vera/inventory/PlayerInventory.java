
package io.vera.inventory;


import javax.annotation.Nonnull;


public interface PlayerInventory extends Inventory {

    @Nonnull
    Item getHeldItem();

    @Nonnull
    Item getOffHeldItem();

    int getSelectedSlot();
}