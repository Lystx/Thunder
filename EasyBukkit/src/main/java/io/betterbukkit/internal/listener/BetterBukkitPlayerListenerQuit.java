package io.betterbukkit.internal.listener;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.events.PlayerConnectionStateChangeEvent;
import io.betterbukkit.provider.player.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BetterBukkitPlayerListenerQuit implements Listener {

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        Player player = EasyBukkit.getInstance().getPlayer(event.getPlayer().getName());

        PlayerConnectionStateChangeEvent stateChangeEvent = new PlayerConnectionStateChangeEvent(player, PlayerConnectionStateChangeEvent.State.QUIT);
        EasyBukkit.getInstance().getEventProvider().callEvent(stateChangeEvent);

        EasyBukkit.getInstance().getPlayers().remove(player);
    }
}
