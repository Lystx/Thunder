
package io.vera.meta.entity;

import io.vera.world.other.Vector;

public interface FallingBlockMeta extends EntityMeta {

    Vector getSpawnPosition();

    void setSpawnPosition(Vector vector);

}
