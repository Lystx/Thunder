package io.betterbukkit.internal.listener;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.events.PlayerConnectionStateChangeEvent;
import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.util.PlayerUtils;
import io.betterbukkit.provider.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BetterBukkitPlayerListenerLogin implements Listener {

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        Player player = Util.get(PlayerUtils.class).from(event.getPlayer());
        EasyBukkit.getInstance().getPlayers().add(player);

        PlayerConnectionStateChangeEvent stateChangeEvent = new PlayerConnectionStateChangeEvent(player, PlayerConnectionStateChangeEvent.State.LOGIN);
        if (EasyBukkit.getInstance().getEventProvider().callEvent(stateChangeEvent)) {
            event.setKickMessage(stateChangeEvent.getMessage());
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        }

    }
}
