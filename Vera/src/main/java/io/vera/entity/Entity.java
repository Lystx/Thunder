
package io.vera.entity;

import io.vera.world.other.Position;
import io.vera.meta.entity.EntityMeta;
import io.vera.server.world.World;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public interface Entity {

    int getId();

    Position getPosition();

    void setPosition(Position position);

    boolean isOnGround();

    World getWorld();

    void remove();

    EntityMeta getMetadata();

    void updateMetadata();
}