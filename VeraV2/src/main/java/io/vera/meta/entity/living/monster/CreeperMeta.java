package io.vera.meta.entity.living.monster;

public interface CreeperMeta extends MonsterMeta {
  int getCreeperState();
  
  void setCreeperState(int paramInt);
  
  boolean isCharged();
  
  void setCharged(boolean paramBoolean);
  
  boolean isIgnited();
  
  void setIgnited(boolean paramBoolean);
}
