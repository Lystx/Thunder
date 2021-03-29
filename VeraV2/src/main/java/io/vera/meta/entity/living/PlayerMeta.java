package io.vera.meta.entity.living;

public interface PlayerMeta extends LivingEntityMeta {
  float getAdditionalHearts();
  
  void setAdditionalHearts(float paramFloat);
  
  int getScore();
  
  void setScore(int paramInt);
  
  byte getSkinFlags();
  
  void setSkinFlags(byte paramByte);
  
  boolean isCapeEnabled();
  
  void setCapeEnabled(boolean paramBoolean);
  
  boolean isJacketEnabled();
  
  void setJacketEnabled(boolean paramBoolean);
  
  boolean isLeftSleeveEnabled();
  
  void setLeftSleeveEnabled(boolean paramBoolean);
  
  boolean isRightSleeveEnabled();
  
  void setRightSleeveEnabled(boolean paramBoolean);
  
  boolean isLeftLegPantsEnabled();
  
  void setLeftLegPantsEnabled(boolean paramBoolean);
  
  boolean isRightLegPantsEnabled();
  
  void setRightLegPantsEnabled(boolean paramBoolean);
  
  boolean isHatEnabled();
  
  void setHatEnabled(boolean paramBoolean);
  
  boolean isLeftHandMain();
  
  void setLeftHandMain(boolean paramBoolean);
}
