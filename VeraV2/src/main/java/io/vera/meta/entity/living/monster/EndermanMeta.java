package io.vera.meta.entity.living.monster;

public interface EndermanMeta extends MonsterMeta {
  int getCarriedBlockID();
  
  void setCarriedBlockID(int paramInt);
  
  int getCarriedBlockData();
  
  void setCarriedBlockData(int paramInt);
  
  boolean isScreaming();
  
  void setScreaming(boolean paramBoolean);
}
