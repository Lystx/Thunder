package io.vera.world.opt;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum Dimension {
    NETHER(-1),
    OVERWORLD(0),
    END(1);

    private final byte b;

    Dimension(int i) {
        this.b = (byte)i;
    }

    public int asInt() {
        return this.b;
    }

    public byte asByte() {
        return this.b;
    }

    @Nonnull
    public static Dimension from(int i) {
        for (Dimension dim : values()) {
            if (dim.b == i)
                return dim;
        }
        throw new IndexOutOfBoundsException(String.format("NBT value out of range for class %s", new Object[] { "n.t.w.o.Dimension" }));
    }
}
