package io.vera.meta.entity.living.monster;

public interface GuardianMeta extends MonsterMeta {
  boolean isRetractingSpikes();
  
  void setRetractingSpikes(boolean paramBoolean);
  
  boolean isElderly();
  
  void setElderly(boolean paramBoolean);
  
  int getTargetEntityID();
  
  void setTargetEntityID();
}
