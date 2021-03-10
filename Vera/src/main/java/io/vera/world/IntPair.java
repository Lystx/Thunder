
package io.vera.world;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class IntPair {

    private final int x;
    private final int z;

    private IntPair(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static IntPair make(int x, int z) {
        return new IntPair(x, z);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntPair) {
            IntPair pair = (IntPair) obj;
            return pair.x == this.x && pair.z == this.z;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.z;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("IntPair{%d,%d}", this.x, this.z);
    }
}
