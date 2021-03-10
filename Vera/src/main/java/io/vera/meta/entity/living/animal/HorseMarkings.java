
package io.vera.meta.entity.living.animal;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;


@Immutable
public enum HorseMarkings {

    WHITE(0),
    CREAMY(1),
    CHESTNUT(2),
    BROWN(3),
    BLACK(4),
    GRAY(5),
    DARK_BROWN(6);

    @Getter
    private final int data;

    HorseMarkings(int data) {
        this.data = data;
    }

    @Nonnull
    public static HorseMarkings of(int id) {
        for (HorseMarkings markings : values()) {
            if (markings.data == id) {
                return markings;
            }
        }

        throw new IllegalArgumentException("no horse markings with id = " + id);
    }
}