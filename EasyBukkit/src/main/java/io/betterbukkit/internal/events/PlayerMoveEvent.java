package io.betterbukkit.internal.events;

import io.betterbukkit.provider.event.Event;
import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.world.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor
public class PlayerMoveEvent extends Event {

    private final Player player;
    private final Position from;
}
