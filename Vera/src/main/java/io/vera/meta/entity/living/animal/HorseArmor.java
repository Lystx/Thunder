
package io.vera.meta.entity.living.animal;

import lombok.Getter;

import javax.annotation.concurrent.Immutable;


@Immutable
public enum HorseArmor {

    LEATHER(3),
    IRON(5),
    GOLD(7),
    DIAMOND(11);

    @Getter
    private final int armor;

    HorseArmor(int armor) {
        this.armor = armor;
    }
}