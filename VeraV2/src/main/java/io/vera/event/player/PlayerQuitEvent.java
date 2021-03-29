package io.vera.event.player;

import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatComponent;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerQuitEvent extends PlayerEvent {

    private ChatComponent message;

    public PlayerQuitEvent(Player player, ChatComponent message) {
        super(player);
        this.message = message;
    }
}
