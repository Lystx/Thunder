package io.vera.meta.entity.living.animal;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum HorseColor {
  NONE(0),
  WHITE(1),
  WHITE_FIELD(2),
  WHITE_DOTS(3),
  BLACK_DOTS(4);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  HorseColor(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static HorseColor of(int id) {
    for (HorseColor color : values()) {
      if (color.data == id)
        return color; 
    } 
    throw new IllegalArgumentException("no horse color with id = " + id);
  }
}
