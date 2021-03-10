
package io.vera.world.opt;

import io.vera.util.Misc;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum LevelType {

    DEFAULT("default"),
    FLAT("flat"),
    LARGE_BIOMES("largeBiomes"),
    AMPLIFIED("amplified"),
    DEBUG("default_1_1");

    private final String s;

    LevelType(String s) {
        this.s = s;
    }

    public static LevelType from(String s) {
        for (LevelType type : values()) {
            if (type.toString().equalsIgnoreCase(s)) return type;
        }

        throw new IllegalArgumentException(String.format(Misc.NBT_BOUND_FAIL, "n.t.w.o.LevelType"));
    }

    @Override
    public String toString() {
        return this.s;
    }
}