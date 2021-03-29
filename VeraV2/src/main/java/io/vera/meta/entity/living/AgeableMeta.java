package io.vera.meta.entity.living;

public interface AgeableMeta extends CreatureMeta {
  boolean isBaby();
  
  void setBaby(boolean paramBoolean);
}
