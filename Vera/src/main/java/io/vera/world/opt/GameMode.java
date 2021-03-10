
package io.vera.world.opt;

import io.vera.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum GameMode {

    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    private final byte b;

    GameMode(int i) {
        this.b = (byte) i;
    }

    public byte asByte() {
        return this.b;
    }

    public int asInt() {
        return this.b;
    }

    @Nonnull
    public static GameMode from(int i) {
        for (GameMode mode : values()) {
            if (mode.b == i) return mode;
        }

        throw new IndexOutOfBoundsException(String.format(Misc.NBT_BOUND_FAIL, "n.t.w.o.GameMode"));
    }
}