
package io.vera.world.opt;

import io.vera.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum Difficulty {

    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3),
    HARDCORE(3);

    private final byte b;

    Difficulty(int i) {
        this.b = (byte) i;
    }

    public byte asByte() {
        return this.b;
    }

    @Nonnull
    public static Difficulty from(int i) {
        for (Difficulty difficulty : values()) {
            if (difficulty.b == i) return difficulty;
        }

        throw new IllegalArgumentException(String.format(Misc.NBT_BOUND_FAIL, "n.t.w.o.Difficulty"));
    }
}