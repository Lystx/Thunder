
package io.vera.event.player;

import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatComponent;

@Getter
@Setter
public class PlayerChatEvent extends PlayerEvent {

    @NonNull
    private ChatComponent chatComponent;

    @NonNull
    private Collection<Player> recipients;

    public PlayerChatEvent(Player player, ChatComponent chatComponent, Collection<Player> recipients) {
        super(player);
        this.chatComponent = Objects.requireNonNull(chatComponent, "chat component cannot be null");
        this.recipients = Objects.requireNonNull(recipients, "recipients cannot be null");
    }

}
