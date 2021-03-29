package io.betterbukkit.internal.listener;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.events.PlayerMoveEvent;
import io.betterbukkit.provider.player.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BetterBukkitPlayerListenerMove implements Listener {

    @EventHandler
    public void handle(org.bukkit.event.player.PlayerMoveEvent event) {

        Player player = EasyBukkit.getInstance().getPlayer(event.getPlayer().getName());

        PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(player, player.getPosition());
        if (EasyBukkit.getInstance().getEventProvider().callEvent(playerMoveEvent)) {
            player.teleport(playerMoveEvent.getFrom());
        }
    }
}
