package io.vera.meta.entity.vehicle;

import io.vera.meta.entity.EntityMeta;

public interface BoatMeta extends EntityMeta {
  int getTimeSinceLastHit();
  
  void setTimeSinceLastHit(int paramInt);
  
  int getForwardDirection();
  
  void setForwardDirection(int paramInt);
  
  float getDamageTaken();
  
  void setDamageTaken(float paramFloat);
  
  int getBoatType();
  
  void setBoatType(int paramInt);
  
  boolean isLeftPaddleTurning();
  
  void setLeftPaddleTurning(boolean paramBoolean);
  
  boolean isRightPaddleTurning();
  
  void setRightPaddleTurning(boolean paramBoolean);
}
