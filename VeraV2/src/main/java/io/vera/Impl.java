package io.vera;

import io.vera.entity.living.Player;
import io.vera.inventory.Inventory;
import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.inventory.Substance;
import io.vera.logger.LogHandler;
import io.vera.logger.Logger;
import io.vera.meta.ItemMeta;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Impl {

    private static final CountDownLatch IMPL_LATCH = new CountDownLatch(1);

    private static Provider impl;

    private static final Object lock = new Object();

    public static void setImpl(Provider i) {
        synchronized (lock) {
            if (impl == null) {
                impl = i;
                IMPL_LATCH.countDown();
            }
        }
    }

    @Nonnull
    public static Provider get() {
        try {
            IMPL_LATCH.await();
            return impl;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static interface Provider {
        Logger newLogger(String param1String);

        void attachHandler(Logger param1Logger, LogHandler param1LogHandler);

        boolean removeHandler(Logger param1Logger, LogHandler param1LogHandler);

        Inventory newInventory(InventoryType param1InventoryType, int param1Int);

        Item newItem(Substance param1Substance, int param1Int, byte param1Byte, ItemMeta param1ItemMeta);

        @Nonnull
        Map<String, Player> findByName(String param1String);

        @Nonnull
        Map<String, Player> findByNameFuzzy(String param1String);

        @Nullable
        Player getByUuid(UUID param1UUID);

        @Nullable
        Player getByName(String param1String);

        PluginChannel open(String param1String, Player... param1VarArgs);

        PluginChannel open(String param1String, Collection<? extends Player> param1Collection);

        PluginChannel openAll(String param1String);

        PluginChannel tryOpen(String param1String);

        PluginChannel get(String param1String);

        void register(SimpleChannelListener param1SimpleChannelListener);

        boolean unregister(Class<? extends SimpleChannelListener> param1Class);
    }
}
