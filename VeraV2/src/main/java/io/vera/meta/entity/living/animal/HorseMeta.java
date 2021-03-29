package io.vera.meta.entity.living.animal;

import java.util.UUID;

public interface HorseMeta extends AnimalMeta {
  boolean isTame();
  
  void setTame(boolean paramBoolean);
  
  boolean isSaddled();
  
  void setSaddled(boolean paramBoolean);
  
  boolean hasChest();
  
  void setHasChest(boolean paramBoolean);
  
  boolean isBred();
  
  void setBred(boolean paramBoolean);
  
  boolean isHorseEating();
  
  void setHorseEating(boolean paramBoolean);
  
  boolean isRearing();
  
  void setRearing(boolean paramBoolean);
  
  boolean isMouthOpen();
  
  void setMouthOpen(boolean paramBoolean);
  
  HorseType getHorseType();
  
  void setHorseType(HorseType paramHorseType);
  
  HorseColor getHorseColor();
  
  void setHorseColor(HorseColor paramHorseColor);
  
  HorseMarkings getHorseMarkings();
  
  void setHorseMarkings(HorseMarkings paramHorseMarkings);
  
  UUID getOwner();
  
  void setOwner(UUID paramUUID);
  
  HorseArmor getHorseArmor();
  
  void setHorseArmor(HorseArmor paramHorseArmor);
}
