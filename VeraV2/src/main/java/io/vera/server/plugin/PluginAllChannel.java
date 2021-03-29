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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class PluginAllChannel implements PluginChannel {

    private final String name;
    private volatile boolean closed;

    @ConstructorProperties({"name"})
    public PluginAllChannel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void close() {
        this.closed = true;
        VeraPluginChannel.remove(this.name);
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : VeraPlayer.getPlayers().values())
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values())
                listener.channelClosed(this, Destination.CLIENT, VeraPlayer.getPlayers().values());
        });
    }

    public void closeFor(Function<Player, Boolean> function) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : VeraPlayer.getPlayers().values()) {
            if (((Boolean)function.apply(player)).booleanValue()) {
                players.add(player);
                ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
            }
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
    }

    public boolean closeFor(Collection<Player> players) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players)
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
        return true;
    }

    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg("UNREGISTER", this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player = (Player)VeraPlayer.getPlayers().get(uuid);
            if (player != null) {
                players.add(player);
                ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
            } else {
                success = false;
            }
        }
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values())
                listener.channelClosed(this, Destination.CLIENT, players);
        });
        return success;
    }

    public void addRecipient(Player... recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    public void addRecipient(Collection<? extends Player> recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    public Collection<Player> getRecipients() {
        return Collections.unmodifiableCollection(VeraPlayer.getPlayers().values());
    }

    public boolean send(byte[] message) {
        if (this.closed)
            return false;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : VeraPlayer.getPlayers().values())
            ((VeraPlayer)player).net().sendPacket((PacketOut)msg);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values())
                listener.messageSent(this, message);
        });
        return true;
    }
}
