
package io.vera.meta.entity.living.monster;



public interface ZombieMeta extends MonsterMeta {

    boolean isBaby();

    void setBaby(boolean baby);

    ZombieType getZombieType();

    void setZombieType(ZombieType type);

    boolean isConverting();

    void setConverting(boolean converting);

    boolean areHandsHeldUp();

    void setHandsHeldUp(boolean handsUp);

}
