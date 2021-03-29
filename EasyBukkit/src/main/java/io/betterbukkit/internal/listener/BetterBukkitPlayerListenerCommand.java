package io.betterbukkit.internal.listener;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.events.PlayerChatEvent;
import io.betterbukkit.provider.player.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BetterBukkitPlayerListenerCommand implements Listener {

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        if (EasyBukkit.getInstance().getCommandProvider().getCommand(event.getMessage().substring(1).split(" ")[0]) != null) {
            event.setCancelled(true);
            Player player = EasyBukkit.getInstance().getPlayer(event.getPlayer().getName());
            EasyBukkit.getInstance().getCommandProvider().execute(player, true, event.getMessage());
        }
    }


    @EventHandler
    public void handle(AsyncPlayerChatEvent event) {
        Player player = EasyBukkit.getInstance().getPlayer(event.getPlayer().getName());
        PlayerChatEvent chatEvent = new PlayerChatEvent(player, event.getMessage());

        if (EasyBukkit.getInstance().getEventProvider().callEvent(chatEvent)) {
            event.setCancelled(true);
            if (!chatEvent.getMessage().equalsIgnoreCase(event.getMessage())) {
                EasyBukkit.getInstance().getPlayers().forEach(p -> p.sendMessage(chatEvent.getMessage()));
            }
        }
    }
}
