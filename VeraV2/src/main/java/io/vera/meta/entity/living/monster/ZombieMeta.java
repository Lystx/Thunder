package io.vera.meta.entity.living.monster;

public interface ZombieMeta extends MonsterMeta {
  boolean isBaby();
  
  void setBaby(boolean paramBoolean);
  
  ZombieType getZombieType();
  
  void setZombieType(ZombieType paramZombieType);
  
  boolean isConverting();
  
  void setConverting(boolean paramBoolean);
  
  boolean areHandsHeldUp();
  
  void setHandsHeldUp(boolean paramBoolean);
}
