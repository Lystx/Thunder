
package io.vera.event.player;

import io.vera.event.base.Event;
import io.vera.event.annotations.Supertype;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import io.vera.entity.living.Player;

import javax.annotation.concurrent.Immutable;


@Immutable
@Supertype
@AllArgsConstructor
public class PlayerEvent extends Event {
    @Getter
    @NonNull
    private final Player player;

}
