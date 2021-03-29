package io.betterbukkit.internal.listener;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.events.PlayerConnectionStateChangeEvent;
import io.betterbukkit.provider.player.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BetterBukkitPlayerListenerJoin implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {

        Player player = EasyBukkit.getInstance().getPlayer(event.getPlayer().getName());

        PlayerConnectionStateChangeEvent stateChangeEvent = new PlayerConnectionStateChangeEvent(player, PlayerConnectionStateChangeEvent.State.JOIN);
        if (EasyBukkit.getInstance().getEventProvider().callEvent(stateChangeEvent)) {
            event.setJoinMessage(stateChangeEvent.getMessage());
        }
    }
}
