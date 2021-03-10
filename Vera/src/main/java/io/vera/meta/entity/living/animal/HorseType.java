
package io.vera.meta.entity.living.animal;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents the different types of Horses.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@Immutable
public enum HorseType {
    /**
     * A horse.
     */
    HORSE(0),

    /**
     * A donkey.
     */
    DONKEY(1),

    /**
     * A mule.
     */
    MULE(2),

    /**
     * A zombie.
     */
    ZOMBIE(3),

    /**
     * A skeleton.
     */
    SKELETON(4);

    @Getter
    private final int data;

    HorseType(int data) {
        this.data = data;
    }

    /**
     * Gets the horse type corresponding to the given internal identification number.
     * <br>
     * If none are found, an {@link IllegalArgumentException} will be thrown.
     *
     * @param id The identification number.
     *
     * @return The horse type.
     */
    @Nonnull
    public static HorseType of(int id) {
        for (HorseType type : values()) {
            if (type.data == id) {
                return type;
            }
        }

        throw new IllegalArgumentException("no horse type with id = " + id);
    }
}