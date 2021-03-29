package io.vera.inventory;

import io.vera.Impl;
import io.vera.ui.chat.ChatComponent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Inventory {

    static Inventory newInventory(InventoryType type, int slots) {
        return Impl.get().newInventory(type, slots);
    }

    boolean add(Item paramItem, int paramInt);

    @Nullable
    Item add(int paramInt1, Item paramItem, int paramInt2);

    @Nonnull
    Item remove(int paramInt1, int paramInt2);

    @Nonnull
    Item get(int paramInt);

    int getSize();

    InventoryType getType();

    ChatComponent getTitle();

    void setTitle(ChatComponent paramChatComponent);
}
