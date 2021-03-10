
package io.vera.meta.entity.living.animal;

import io.vera.meta.DyeColor;



public interface WolfMeta extends TameableAnimalMeta {

    float getDamageTaken();

    void setDamageTaken(float damageTaken);

    boolean isBegging();

    void setBegging(boolean begging);

    DyeColor getCollarColor();

    void setCollarColor(DyeColor color);

}
