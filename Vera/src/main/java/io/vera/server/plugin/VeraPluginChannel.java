
package io.vera.server.plugin;

import io.vera.doc.Policy;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.net.NetData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.vera.entity.living.Player;
import io.vera.plugin.channel.Destination;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;
import io.vera.server.packet.play.PlayOutPluginMsg;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ThreadSafe
@RequiredArgsConstructor
public class VeraPluginChannel implements PluginChannel {

    private static final Map<String, PluginChannel> CHANNELS = new ConcurrentHashMap<>();

    private static final Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> listeners =
            new HashMap<>();

    public static Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> getListeners() {
        return listeners;
    }

    public static final String REGISTER = "REGISTER";
    public static final String UNREGISTER = "UNREGISTER";
    private final Map<UUID, Player> recipients = new ConcurrentHashMap<>();
    @Getter
    private final String name;
    private volatile boolean closed;

    public static PluginChannel getChannel(String name, Function<String, PluginChannel> func) {
        return CHANNELS.computeIfAbsent(name, func);
    }

    public static PluginChannel get(String name) {
        return CHANNELS.get(name);
    }

    public static void remove(String name) {
        CHANNELS.remove(name);
    }

    public static void autoAdd(VeraPlayer player) {
        Set<Player> singleton = Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof PluginAllChannel) {
                PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER,
                        channel.getName().getBytes(NetData.NET_CHARSET));
                player.net().sendPacket(msg);

                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values()) {
                        listener.channelOpened(channel, Destination.CLIENT, singleton);
                    }
                });
            }
        }
    }

    public static void autoRemove(VeraPlayer player) {
        Set<Player> singleton = Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof PluginAllChannel) {
                channel.closeFor(singleton);

                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values()) {
                        listener.channelClosed(channel, Destination.CLIENT, singleton);
                    }
                });
            }
        }
    }

    public static void register(SimpleChannelListener listener) {
        listeners.put(listener.getClass(), listener);
    }

    public static boolean unregister(Class<? extends SimpleChannelListener> cls) {
        return listeners.remove(cls) != null;
    }

    @Override
    public void close() {
        this.closed = true;
        CHANNELS.remove(this.name);

        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            this.recipients.remove(player.getUuid());
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, this.recipients.values());
            }
        });
    }

    @Override
    public void closeFor(Function<Player, Boolean> function) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            if (function.apply(player)) {
                players.add(player);
                this.recipients.remove(player.getUuid());
                ((VeraPlayer) player).net().sendPacket(msg);
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public boolean closeFor(Collection<Player> players) {
        boolean success = true;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            player = this.recipients.remove(player.getUuid());
            if (player != null) {
                ((VeraPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player = this.recipients.remove(uuid);
            if (player != null) {
                players.add(player);
                ((VeraPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public void addRecipient(Player... recipients) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            players.add(player);
            this.recipients.put(player.getUuid(), player);
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelOpened(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public void addRecipient(Collection<? extends Player> recipients) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            this.recipients.put(player.getUuid(), player);
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelOpened(this, Destination.CLIENT, recipients);
            }
        });
    }

    @Override
    public Collection<Player> getRecipients() {
        return Collections.unmodifiableCollection(this.recipients.values());
    }

    @Override
    public boolean send(byte[] message) {
        if (this.closed) {
            return false;
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : this.recipients.values()) {
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.messageSent(this, message);
            }
        });
        return true;
    }
}