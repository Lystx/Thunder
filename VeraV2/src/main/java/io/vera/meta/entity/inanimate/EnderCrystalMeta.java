package io.vera.meta.entity.inanimate;

import io.vera.meta.entity.EntityMeta;
import io.vera.world.other.Vector;

public interface EnderCrystalMeta extends EntityMeta {
  Vector getBeamTarget();
  
  void setBeamTarget(Vector paramVector);
  
  boolean isShowBottom();
  
  void setShowBottom(boolean paramBoolean);
}
