package io.vera.server.plugin;

import io.vera.entity.living.Player;
import io.vera.plugin.channel.Destination;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutPluginMsg;
import io.vera.server.player.VeraPlayer;
import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraPluginChannel implements PluginChannel {

    public VeraPluginChannel(String name) {
        this.recipients = new ConcurrentHashMap<>();
        this.name = name;
    }

    private static final Map<String, PluginChannel> CHANNELS = new ConcurrentHashMap<>();
    private static final Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> listeners = new HashMap<>();
    private final Map<UUID, Player> recipients;
    private final String name;
    private volatile boolean closed;

    public static Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> getListeners() {
        return listeners;
    }

    public String getName() {
        return this.name;
    }

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
        Set<Player> singleton = (Set)Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof PluginAllChannel) {
                PlayOutPluginMsg msg = new PlayOutPluginMsg("REGISTER", channel.getName().getBytes(NetData.NET_CHARSET));
                player.net().sendPacket((PacketOut)msg);
                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values())
                        listener.channelOpened(channel, Destination.CLIENT, singleton);
                });
            }
        }
    }

    public static void autoRemove(VeraPlayer player) {
        Set<Player> singleton = (Set)Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof PluginAllChannel) {
                channel.closeFor(singleton);
                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values())
                        listener.channelClosed(channel, Destination.CLIENT, singleton);
                });
            }
        }
    }

    public static void register(SimpleChannelListener listener) {
        listeners.put(listener.getClass(), listener);
    }

    public static boolean unregister(Class<? extends SimpleChannelListener> cls) {
        return (listeners.remove(cls) != null);
    }

    public void close() {
        this.closed = true;
        CHANNELS.remove(this.name);
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            this.recipients.remove(player.getUuid());
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelClosed(this, Destination.CLIENT, this.recipients.values());
        });
    }

    public void closeFor(Function<Player, Boolean> function) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            if (((Boolean)function.apply(player)).booleanValue()) {
                players.add(player);
                this.recipients.remove(player.getUuid());
                ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
            }
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
    }

    public boolean closeFor(Collection<Player> players) {
        boolean success = true;
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            player = this.recipients.remove(player.getUuid());
            if (player != null) {
                ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
                continue;
            }
            success = false;
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
        return success;
    }

    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player = this.recipients.remove(uuid);
            if (player != null) {
                players.add(player);
                ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
            } else {
                success = false;
            }
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
        return success;
    }

    public void addRecipient(Player... recipients) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg("REGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            players.add(player);
            this.recipients.put(player.getUuid(), player);
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelOpened(this, Destination.CLIENT, players);
        });
    }

    public void addRecipient(Collection<? extends Player> recipients) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg("REGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            this.recipients.put(player.getUuid(), player);
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.channelOpened(this, Destination.CLIENT, recipients);
        });
    }

    public Collection<Player> getRecipients() {
        return Collections.unmodifiableCollection(this.recipients.values());
    }

    public boolean send(byte[] message) {
        if (this.closed)
            return false;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : this.recipients.values())
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values())
                listener.messageSent(this, message);
        });
        return true;
    }
}
