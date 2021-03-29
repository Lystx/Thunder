package io.betterbukkit.internal.events;

import io.betterbukkit.provider.event.Event;
import io.betterbukkit.provider.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor
@Setter
public class PlayerConnectionStateChangeEvent extends Event {

    private final Player player;
    private final State state;
    private String message = "No reason defined!";

    public enum State {

        LOGIN,
        JOIN,
        QUIT
    }
}
