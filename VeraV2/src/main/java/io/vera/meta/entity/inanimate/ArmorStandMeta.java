package io.vera.meta.entity.inanimate;

import io.vera.meta.entity.living.LivingEntityMeta;
import io.vera.world.other.Vector;

public interface ArmorStandMeta extends LivingEntityMeta {
  boolean isSmall();
  
  void setSmall(boolean paramBoolean);
  
  boolean hasArms();
  
  void setHasArms(boolean paramBoolean);
  
  boolean hasBasePlate();
  
  void setHasBasePlate(boolean paramBoolean);
  
  boolean hasMarker();
  
  void setHasMarker(boolean paramBoolean);
  
  Vector getHeadRotation();
  
  void setHeadRotation(Vector paramVector);
  
  Vector getBodyRotation();
  
  void setBodyRotation(Vector paramVector);
  
  Vector getLeftArmRotation();
  
  void setLeftArmRotation(Vector paramVector);
  
  Vector getRightArmRotation();
  
  void setRightArmRotation(Vector paramVector);
  
  Vector getLeftLegRotation();
  
  void setLeftLegRotation(Vector paramVector);
  
  Vector getRightLegRotation();
  
  void setRightLegRotation(Vector paramVector);
}
