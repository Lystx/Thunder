package io.vera.meta.entity.living.monster;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum SkeletonType {
  NORMAL(0),
  WITHER(1),
  STRAY(2);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  SkeletonType(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static SkeletonType of(int id) {
    for (SkeletonType type : values()) {
      if (type.data == id)
        return type; 
    } 
    throw new IllegalArgumentException("no skeleton type with id = " + id);
  }
}
