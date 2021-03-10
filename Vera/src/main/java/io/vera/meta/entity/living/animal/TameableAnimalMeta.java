
package io.vera.meta.entity.living.animal;

import java.util.UUID;



public interface TameableAnimalMeta extends AnimalMeta {

    boolean isSitting();

    void setSitting(boolean sitting);

    boolean isAngry();

    void setAngry(boolean angry);

    boolean isTamed();

    void setTamed(boolean tamed);

    UUID getOwner();

    void setOwner(UUID owner);

}
