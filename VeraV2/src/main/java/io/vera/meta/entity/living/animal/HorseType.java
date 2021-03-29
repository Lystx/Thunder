package io.vera.meta.entity.living.animal;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum HorseType {
  HORSE(0),
  DONKEY(1),
  MULE(2),
  ZOMBIE(3),
  SKELETON(4);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  HorseType(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static HorseType of(int id) {
    for (HorseType type : values()) {
      if (type.data == id)
        return type; 
    } 
    throw new IllegalArgumentException("no horse type with id = " + id);
  }
}
