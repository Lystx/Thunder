
package io.vera.meta.entity.living.monster;



public interface SkeletonMeta extends MonsterMeta {

    SkeletonType getSkeletonType();

    void setSkeletonType(SkeletonType type);

    boolean isSwingingArms();

    void setSwingingArms(boolean swingingArms);

}
