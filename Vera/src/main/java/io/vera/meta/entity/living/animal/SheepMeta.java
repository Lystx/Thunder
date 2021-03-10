
package io.vera.meta.entity.living.animal;

import io.vera.meta.DyeColor;



public interface SheepMeta extends AnimalMeta {

    DyeColor getSheepColor();

    void setSheepColor(DyeColor color);

    boolean isSheared();

    void setSheared(boolean sheared);

}
