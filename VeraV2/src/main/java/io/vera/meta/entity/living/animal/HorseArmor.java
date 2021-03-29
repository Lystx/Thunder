package io.vera.meta.entity.living.animal;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum HorseArmor {
  LEATHER(3),
  IRON(5),
  GOLD(7),
  DIAMOND(11);
  
  private final int armor;
  
  public int getArmor() {
    return this.armor;
  }
  
  HorseArmor(int armor) {
    this.armor = armor;
  }
}
