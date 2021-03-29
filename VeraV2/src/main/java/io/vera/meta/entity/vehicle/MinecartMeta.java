package io.vera.meta.entity.vehicle;

import io.vera.meta.entity.EntityMeta;

public interface MinecartMeta extends EntityMeta {
  int getShakingPower();
  
  void setShakingPower(int paramInt);
  
  int getShakingDirection();
  
  void setShakingDirection(int paramInt);
  
  float getShakingMultiplier();
  
  void setShakingMultiplier(boolean paramBoolean);
  
  int getMinecartBlockID();
  
  void setMinecartBlockID(int paramInt);
  
  int getMinecartBlockData();
  
  void setMinecartBlockData(int paramInt);
  
  int getMinecartBlockY();
  
  void setMinecartBlockY(int paramInt);
  
  boolean isShowBlock();
  
  void setShowBlock(boolean paramBoolean);
}
