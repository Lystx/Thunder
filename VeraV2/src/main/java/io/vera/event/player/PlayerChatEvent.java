package io.vera.event.player;

import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatComponent;
import java.util.Collection;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class PlayerChatEvent extends PlayerEvent {

    private ChatComponent chatComponent;
    private Collection<Player> recipients;

    public void setChatComponent(@NonNull ChatComponent chatComponent) {
        if (chatComponent == null) throw new NullPointerException("chatComponent");
        this.chatComponent = chatComponent;
    }

    public void setRecipients(@NonNull Collection<Player> recipients) {
        if (recipients == null) throw new NullPointerException("recipients");
        this.recipients = recipients;
    }

    public PlayerChatEvent(Player player, ChatComponent chatComponent, Collection<Player> recipients) {
        super(player);
        this.chatComponent = Objects.<ChatComponent>requireNonNull(chatComponent, "chat component cannot be null");
        this.recipients = Objects.<Collection<Player>>requireNonNull(recipients, "recipients cannot be null");
    }
}
