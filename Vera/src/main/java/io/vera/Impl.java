
package io.vera;

import io.vera.inventory.Substance;
import io.vera.doc.Internal;
import io.vera.inventory.Inventory;
import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.meta.ItemMeta;
import io.vera.entity.living.Player;
import io.vera.logger.LogHandler;
import io.vera.logger.Logger;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Internal
@ThreadSafe
public final class Impl {

    private static final CountDownLatch IMPL_LATCH = new CountDownLatch(1);

    private static Provider impl;

    private static final Object lock = new Object();

    private Impl() {
    }


    public static void setImpl(Provider i) {

        synchronized (lock) {
            if (Impl.impl == null) {
                Impl.impl = i;
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

    @Internal
    public interface Provider {


        Logger newLogger(String s);
        void attachHandler(Logger logger, LogHandler handler);
        boolean removeHandler(Logger logger, LogHandler handler);

        Inventory newInventory(InventoryType type, int slots);
        Item newItem(Substance substance, int count, byte damage, ItemMeta meta);

        @Nonnull
        Map<String, Player> findByName(String name);

        @Nonnull
        Map<String, Player> findByNameFuzzy(String name);

        @Nullable
        Player getByUuid(UUID uuid);
        @Nullable
        Player getByName(String name);

        PluginChannel open(String name, Player... targets);
        PluginChannel open(String name, Collection<? extends Player> players);
        PluginChannel openAll(String name);
        PluginChannel tryOpen(String name);
        PluginChannel get(String name);

        void register(SimpleChannelListener listener);
        boolean unregister(Class<? extends SimpleChannelListener> cls);
    }
}
