package io.vera.meta.entity.living.animal;

import io.vera.meta.DyeColor;

public interface WolfMeta extends TameableAnimalMeta {
  float getDamageTaken();
  
  void setDamageTaken(float paramFloat);
  
  boolean isBegging();
  
  void setBegging(boolean paramBoolean);
  
  DyeColor getCollarColor();
  
  void setCollarColor(DyeColor paramDyeColor);
}
