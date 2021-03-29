package io.vera.meta.entity.living.animal;

import java.util.UUID;

public interface TameableAnimalMeta extends AnimalMeta {
  boolean isSitting();
  
  void setSitting(boolean paramBoolean);
  
  boolean isAngry();
  
  void setAngry(boolean paramBoolean);
  
  boolean isTamed();
  
  void setTamed(boolean paramBoolean);
  
  UUID getOwner();
  
  void setOwner(UUID paramUUID);
}
