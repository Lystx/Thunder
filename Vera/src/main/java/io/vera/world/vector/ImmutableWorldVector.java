
package io.vera.world.vector;

import io.vera.server.world.World;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Getter
@Immutable
@AllArgsConstructor
public class ImmutableWorldVector {

    private final World world;
    private final int x;
    private final int y;
    private final int z;
}