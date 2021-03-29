package io.vera.entity;

import io.vera.meta.entity.EntityMeta;
import io.vera.server.world.World;
import io.vera.world.other.Position;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Entity {

    int getId();

    Position getPosition();

    void setPosition(Position paramPosition);

    boolean isOnGround();

    World getWorld();

    void remove();

    EntityMeta getMetadata();

    void updateMetadata();
}
