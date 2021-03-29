package io.vera.meta;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum DyeColor {
  BLACK(0),
  RED(1),
  DARK_GREEN(2),
  BROWN(3),
  DARK_BLUE(4),
  DARK_PURPLE(5),
  CYAN(6),
  LIGHT_GRAY(7),
  DARK_GRAY(8),
  PINK(9),
  LIGHT_GREEN(10),
  YELLOW(11),
  LIGHT_BLUE(12),
  MAGENTA(13),
  ORANGE(14),
  WHITE(15);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  DyeColor(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static DyeColor byID(int id) {
    for (DyeColor color : values()) {
      if (color.data == id)
        return color; 
    } 
    throw new IllegalArgumentException("no dye color with id = " + id);
  }
}
