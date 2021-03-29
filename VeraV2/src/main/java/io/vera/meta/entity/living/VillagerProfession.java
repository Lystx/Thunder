package io.vera.meta.entity.living;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum VillagerProfession {
  FARMER(0),
  LIBRARIAN(1),
  PRIEST(2),
  BLACKSMITH(3),
  BUTCHER(4);
  
  private final int data;
  
  public int getData() {
    return this.data;
  }
  
  VillagerProfession(int data) {
    this.data = data;
  }
  
  @Nonnull
  public static VillagerProfession of(int id) {
    for (VillagerProfession prof : values()) {
      if (prof.data == id)
        return prof; 
    } 
    throw new IllegalArgumentException("no villager profession with id = " + id);
  }
}
