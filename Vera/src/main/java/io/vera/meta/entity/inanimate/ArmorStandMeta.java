
package io.vera.meta.entity.inanimate;

import io.vera.world.other.Vector;
import io.vera.meta.entity.living.LivingEntityMeta;



public interface ArmorStandMeta extends LivingEntityMeta {

    boolean isSmall();

    void setSmall(boolean small);

    boolean hasArms();

    void setHasArms(boolean hasArms);

    boolean hasBasePlate();

    void setHasBasePlate(boolean basePlate);

    boolean hasMarker();

    void setHasMarker(boolean hasMarker);

    Vector getHeadRotation();

    void setHeadRotation(Vector rotation);

    Vector getBodyRotation();

    void setBodyRotation(Vector rotation);

    Vector getLeftArmRotation();

    void setLeftArmRotation(Vector rotation);

    Vector getRightArmRotation();

    void setRightArmRotation(Vector rotation);

    Vector getLeftLegRotation();

    void setLeftLegRotation(Vector rotation);

    Vector getRightLegRotation();

    void setRightLegRotation(Vector rotation);

}
