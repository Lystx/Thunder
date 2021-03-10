
package io.vera.inventory;

import io.vera.ui.chat.ChatComponent;
import io.vera.Impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Inventory {

    static Inventory newInventory(InventoryType type, int slots) {
        return Impl.get().newInventory(type, slots);
    }

    boolean add(Item item, int quantity);

    @Nullable
    Item add(int slot, Item item, int quantity);

    @Nonnull
    Item remove(int slot, int quantity);

    @Nonnull
    Item get(int slot);

    int getSize();

    InventoryType getType();

    ChatComponent getTitle();

    void setTitle(ChatComponent title);
}