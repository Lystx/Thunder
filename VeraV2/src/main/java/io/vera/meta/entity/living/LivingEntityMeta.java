package io.vera.meta.entity.living;

import io.vera.meta.entity.EntityMeta;

public interface LivingEntityMeta extends EntityMeta {
  boolean isHandActive();
  
  void setHandActive(boolean paramBoolean);
  
  boolean isMainHandActive();
  
  void setMainHandActive(boolean paramBoolean);
  
  float getHealth();
  
  void setHealth(float paramFloat);
  
  int getPotionEffectColor();
  
  void setPotionEffectColor(int paramInt);
  
  boolean isPotionEffectAmbient();
  
  void setPotionEffectAmbient(boolean paramBoolean);
  
  int getNumberOfArrowsInEntity();
  
  void setNumberOfArrowsInEntity(int paramInt);
}
