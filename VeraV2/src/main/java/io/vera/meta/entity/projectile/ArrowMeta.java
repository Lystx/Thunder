package io.vera.meta.entity.projectile;

import io.vera.meta.entity.EntityMeta;

public interface ArrowMeta extends EntityMeta {
  boolean isCritical();
  
  void setCritical(boolean paramBoolean);
}
