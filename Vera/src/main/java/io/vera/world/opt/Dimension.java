
package io.vera.world.opt;

import io.vera.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * The set of dimensions (not size but world type) that a
 * world can be set to.
 *
 * @author TridentSDK
 * @since 0.3-alpha-DP
 */
@Immutable
public enum Dimension {
    /**
     * Represents the Nether dimension
     *
     * <p>NBT Value: {@code -1}</p>
     */
    NETHER(-1),
    /**
     * Represents the overworld (or normal) dimension
     *
     * <p>NBT Value: {@code 0}</p>
     */
    OVERWORLD(0),
    /**
     * Represents the End dimension
     *
     * <p>NBT Value: {@code 1}</p>
     */
    END(1);

    /**
     * The NBT value of the dimension
     */
    private final byte b;

    /**
     * Creates a new dimension that uses the given
     * {@code int} as the NBT value.
     *
     * @param i the NBT value of the dimension
     */
    Dimension(int i) {
        this.b = (byte) i;
    }

    /**
     * Obtains the NBT value of the dimension represented as
     * an {@code int}.
     *
     * @return the NBT value of the dimension
     */
    public int asInt() {
        return this.b;
    }

    /**
     * Obtains the NBT value of the dimension represented as
     * a {@code byte}.
     *
     * @return the byte form of the NBT value
     */
    public byte asByte() {
        return this.b;
    }

    /**
     * Obtains the dimension that has the given {@code int}
     * associated as its NBT value.
     *
     * @param i NBT value of the dimension to find
     * @return the Dimension, if found
     * @throws IndexOutOfBoundsException if the Dimension is
     *         not found
     */
    @Nonnull
    public static Dimension from(int i) {
        for (Dimension dim : values()) {
            if (dim.b == i) return dim;
        }

        throw new IndexOutOfBoundsException(String.format(Misc.NBT_BOUND_FAIL, "n.t.w.o.Dimension"));
    }
}