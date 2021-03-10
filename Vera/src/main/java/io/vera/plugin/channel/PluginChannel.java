
package io.vera.plugin.channel;

import io.vera.Impl;
import io.vera.entity.living.Player;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

@ThreadSafe
public interface PluginChannel {
    static PluginChannel open(String name, Player... targets) {
        return Impl.get().open(name, targets);
    }

    static PluginChannel open(String name, Collection<Player> targets) {
        return Impl.get().open(name, targets);
    }

    static PluginChannel openAll(String name) {
        return Impl.get().openAll(name);
    }

    @Nullable
    static PluginChannel tryOpen(String name) {
        return Impl.get().tryOpen(name);
    }

    @Nullable
    static PluginChannel get(String name) {
        return Impl.get().get(name);
    }

    void close();

    void closeFor(Function<Player, Boolean> function);

    boolean closeFor(Collection<Player> players);

    boolean closeFor(UUID... uuids);

    String getName();

    void addRecipient(Player... recipients);

    void addRecipient(Collection<? extends Player> recipients);

    Collection<Player> getRecipients();

    boolean send(byte[] message);
}