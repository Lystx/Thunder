package io.vera.meta.entity;

public interface EntityMeta {
  boolean isOnFire();
  
  void setOnFire(boolean paramBoolean);
  
  boolean isCrouched();
  
  void setCrouched(boolean paramBoolean);
  
  boolean isSprinting();
  
  void setSprinting(boolean paramBoolean);
  
  boolean isEating();
  
  void setEating(boolean paramBoolean);
  
  boolean isInvisible();
  
  void setInvisible(boolean paramBoolean);
  
  boolean isGlowing();
  
  void setGlowing(boolean paramBoolean);
  
  boolean isUsingElytra();
  
  void setUsingElytra(boolean paramBoolean);
  
  int getAir();
  
  void setAir(int paramInt);
  
  String getCustomName();
  
  void setCustomName(String paramString);
  
  boolean isCustomNameVisible();
  
  void setCustomNameVisible(boolean paramBoolean);
  
  boolean isSilent();
  
  void setSilent(boolean paramBoolean);
  
  boolean isNoGravity();
  
  void setNoGravity(boolean paramBoolean);
}
