
package io.vera.server.plugin;

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
import java.util.function.Function;


@ThreadSafe
@RequiredArgsConstructor
public class PluginAllChannel implements PluginChannel {

    @Getter
    private final String name;

    private volatile boolean closed;

    @Override
    public void close() {
        this.closed = true;
        VeraPluginChannel.remove(this.name);

        PlayOutPluginMsg msg = new PlayOutPluginMsg(VeraPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : VeraPlayer.getPlayers().values()) {
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, VeraPlayer.getPlayers().values());
            }
        });
    }

    @Override
    public void closeFor(Function<Player, Boolean> function) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(VeraPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : VeraPlayer.getPlayers().values()) {
            if (function.apply(player)) {
                players.add(player);
                ((VeraPlayer) player).net().sendPacket(msg);
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  VeraPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public boolean closeFor(Collection<Player> players) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg(VeraPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  VeraPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return true;
    }

    @Override
    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(VeraPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player = VeraPlayer.getPlayers().get(uuid);
            if (player != null) {
                players.add(player);
                ((VeraPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  VeraPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public void addRecipient(Player... recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    @Override
    public void addRecipient(Collection<? extends Player> recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    @Override
    public Collection<Player> getRecipients() {
        return Collections.unmodifiableCollection(VeraPlayer.getPlayers().values());
    }

    @Override
    public boolean send(byte[] message) {
        if (this.closed) {
            return false;
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : VeraPlayer.getPlayers().values()) {
            ((VeraPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values()) {
                listener.messageSent(this, message);
            }
        });
        return true;
    }
}
