package io.vera.meta.entity.living.animal;

import io.vera.meta.DyeColor;

public interface SheepMeta extends AnimalMeta {
  DyeColor getSheepColor();
  
  void setSheepColor(DyeColor paramDyeColor);
  
  boolean isSheared();
  
  void setSheared(boolean paramBoolean);
}
