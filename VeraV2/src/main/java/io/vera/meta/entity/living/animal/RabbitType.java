package io.vera.meta.entity.living.animal;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum RabbitType {
  BROWN(0),
  WHITE(1),
  BLACK(2),
  BLACK_AND_WHITE(3),
  GOLD(4),
  SALT_AND_PEPPER(5),
  KILLER_BUNNY(99);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  RabbitType(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static RabbitType of(int id) {
    for (RabbitType type : values()) {
      if (type.data == id)
        return type; 
    } 
    throw new IllegalArgumentException("no rabbit type with id = " + id);
  }
}
