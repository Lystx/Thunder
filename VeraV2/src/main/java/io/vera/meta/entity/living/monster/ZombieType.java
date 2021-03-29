package io.vera.meta.entity.living.monster;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum ZombieType {
  NORMAL(0),
  VILLAGER_FARMER(1),
  VILLAGER_LIBRARIAN(2),
  VILLAGER_PRIEST(3),
  VILLAGER_BLACKSMITH(4),
  VILLAGER_BUTCHER(5),
  HUSK(6);
  
  private final int data;
  
  private final boolean villager;
  
  public int getData() {
    return this.data;
  }
  
  public boolean isVillager() {
    return this.villager;
  }
  
  ZombieType(int data) {
    this.data = data;
    this.villager = (data >= 1 && data <= 5);
  }
  
  @Nonnull
  public static ZombieType of(int id) {
    for (ZombieType type : values()) {
      if (type.data == id)
        return type; 
    } 
    throw new IllegalArgumentException("no zombie type with id = " + id);
  }
}
