
package io.vera.meta.entity.living;

import io.vera.meta.entity.EntityMeta;



public interface LivingEntityMeta extends EntityMeta {

    boolean isHandActive();

    void setHandActive(boolean active);

    boolean isMainHandActive();

    void setMainHandActive(boolean mainHand);

    float getHealth();

    void setHealth(float health);

    int getPotionEffectColor();

    void setPotionEffectColor(int potionEffectColor);

    boolean isPotionEffectAmbient();

    void setPotionEffectAmbient(boolean ambient);

    int getNumberOfArrowsInEntity();

    void setNumberOfArrowsInEntity(int arrows);

}
