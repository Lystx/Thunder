
package io.vera.meta.entity.living.animal;

import java.util.UUID;



public interface HorseMeta extends AnimalMeta {

    boolean isTame();

    void setTame(boolean tame);

    boolean isSaddled();

    void setSaddled(boolean saddled);

    boolean hasChest();

    void setHasChest(boolean hasChest);

    boolean isBred();

    void setBred(boolean isBred);

    boolean isHorseEating();

    void setHorseEating(boolean eating);

    boolean isRearing();

    void setRearing(boolean rearing);

    boolean isMouthOpen();

    void setMouthOpen(boolean mouthOpen);

    HorseType getHorseType();

    void setHorseType(HorseType type);

    HorseColor getHorseColor();

    void setHorseColor(HorseColor color);

    HorseMarkings getHorseMarkings();

    void setHorseMarkings(HorseMarkings markings);

    UUID getOwner();

    void setOwner(UUID uuid);

    HorseArmor getHorseArmor();

    void setHorseArmor(HorseArmor armor);

}
