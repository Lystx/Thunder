package io.vera.meta.entity.living.monster;

public interface WitherMeta extends MonsterMeta {
  int getFirstHeadTarget();
  
  void setFirstHeadTarget(int paramInt);
  
  int getSecondHeadTarget();
  
  void setSecondHeadTarget(int paramInt);
  
  int getThirdHeadTarget();
  
  void setThirdHeadTarget(int paramInt);
  
  int getInvulnerableTime();
  
  void setInvulnerableTime(int paramInt);
}
