package io.vera.plugin.channel;

import io.vera.Impl;
import io.vera.entity.living.Player;
import java.util.Collection;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class SimpleChannelListener {
    public static void register(SimpleChannelListener listener) {
        Impl.get().register(listener);
    }

    public static boolean unregister(Class<? extends SimpleChannelListener> cls) {
        return Impl.get().unregister(cls);
    }

    public void channelOpened(PluginChannel channel, Destination dest, Collection<? extends Player> players) {}

    public void channelClosed(PluginChannel channel, Destination dest, Collection<? extends Player> players) {}

    public void messageReceived(String channel, Player sender, byte[] message) {}

    public void messageSent(PluginChannel channel, byte[] message) {}

    public abstract boolean listenFor(PluginChannel paramPluginChannel, Destination paramDestination);
}
