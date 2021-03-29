package io.vera.world;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable @Getter @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntPair {

    private final int x;
    private final int z;

    public static IntPair make(int x, int z) {
        return new IntPair(x, z);
    }

    public boolean equals(Object obj) {
        if (obj instanceof IntPair) {
            IntPair pair = (IntPair)obj;
            return (pair.x == this.x && pair.z == this.z);
        }
        return false;
    }

}
