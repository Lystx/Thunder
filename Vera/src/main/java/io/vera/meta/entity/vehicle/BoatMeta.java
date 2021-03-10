
package io.vera.meta.entity.vehicle;

import io.vera.meta.entity.EntityMeta;


public interface BoatMeta extends EntityMeta {

    int getTimeSinceLastHit();

    void setTimeSinceLastHit(int timeSinceLastHit);

    int getForwardDirection();

    void setForwardDirection(int forwardDirection);

    float getDamageTaken();

    void setDamageTaken(float damageTaken);

    int getBoatType();

    void setBoatType(int boatType);

    boolean isLeftPaddleTurning();

    void setLeftPaddleTurning(boolean leftPaddleTurning);

    boolean isRightPaddleTurning();

    void setRightPaddleTurning(boolean rightPaddleTurning);

}
