
package io.vera.entity.living;

import io.vera.entity.Entity;


public interface EntityLiving extends Entity {

    void setSprinting(boolean sprinting);

    boolean isSprinting();

    void setCrouching(boolean crouching);

    boolean isCrouching();
}