package io.vera.meta.entity.inanimate;

import io.vera.meta.entity.EntityMeta;
import java.awt.Color;

public interface AreaEffectCloudMeta extends EntityMeta {
  float getRadius();
  
  void setRadius(float paramFloat);
  
  Color getColor();
  
  void setColor(Color paramColor);
  
  boolean isSinglePoint();
  
  void setSinglePoint(boolean paramBoolean);
  
  int getParticleID();
  
  void setParticleID(int paramInt);
  
  int getParticleParameter1();
  
  void setParticleParameter1(int paramInt);
  
  int getParticleParameter2();
  
  void setParticleParameter2(int paramInt);
}
