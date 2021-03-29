package io.vera.meta.entity.living;

public interface InsentientEntityMeta extends LivingEntityMeta {
  boolean isNoAI();
  
  void setNoAI(boolean paramBoolean);
  
  boolean isLeftHanded();
  
  void setLeftHanded(boolean paramBoolean);
}
