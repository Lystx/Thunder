package io.vera.world.opt;

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
            if (type.toString().equalsIgnoreCase(s))
                return type;
        }
        throw new IllegalArgumentException(String.format("NBT value out of range for class %s", new Object[] { "n.t.w.o.LevelType" }));
    }

    public String toString() {
        return this.s;
    }
}
