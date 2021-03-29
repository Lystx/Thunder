package io.vera.meta.entity.living.monster;

public interface SkeletonMeta extends MonsterMeta {
  SkeletonType getSkeletonType();
  
  void setSkeletonType(SkeletonType paramSkeletonType);
  
  boolean isSwingingArms();
  
  void setSwingingArms(boolean paramBoolean);
}
