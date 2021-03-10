
package io.vera.event.player;

import lombok.Getter;
import lombok.Setter;
import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatComponent;

@Getter
public class PlayerJoinEvent extends PlayerEvent {

    @Setter private ChatComponent message;

    public PlayerJoinEvent(Player player, ChatComponent message) {
        super(player);
        this.message = message;
    }

}
