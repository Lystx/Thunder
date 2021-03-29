package io.vera.event.player;

import io.vera.entity.living.Player;
import io.vera.event.annotations.Supertype;
import io.vera.event.base.Event;
import java.beans.ConstructorProperties;
import javax.annotation.concurrent.Immutable;

import lombok.Getter;
import lombok.NonNull;

@Supertype
@Immutable @Getter
public class PlayerEvent extends Event {

    private final Player player;

    @ConstructorProperties({"player"})
    public PlayerEvent(@NonNull Player player) {
        this.player = player;
    }

}
