package de.lystx.test;

import io.betterbukkit.internal.events.PlayerChatEvent;
import io.betterbukkit.internal.events.PlayerConnectionStateChangeEvent;
import io.betterbukkit.provider.event.EventListener;
import io.betterbukkit.provider.event.HandleEvent;
import io.betterbukkit.provider.player.Player;

public class TestListener implements EventListener {


    @HandleEvent
    public void handle(PlayerConnectionStateChangeEvent event) {
        Player player = event.getPlayer();

        switch (event.getState()) {
            case JOIN:
                player.sendMessage("WELCOME");
                break;
            case LOGIN:
                break;
            case QUIT:
                System.out.println(player.getName() + " LEFT!");
        }
    }

    @HandleEvent
    public void chat(PlayerChatEvent event) {
        event.setCancelled(true);
        event.setMessage("§7" + event.getPlayer().getName() + " §8: §a" + event.getMessage());
    }

}
