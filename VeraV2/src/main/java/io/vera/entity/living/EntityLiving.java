package io.vera.entity.living;

import io.vera.entity.Entity;

public interface EntityLiving extends Entity {

    void setSprinting(boolean paramBoolean);

    boolean isSprinting();

    void setCrouching(boolean paramBoolean);

    boolean isCrouching();
}
