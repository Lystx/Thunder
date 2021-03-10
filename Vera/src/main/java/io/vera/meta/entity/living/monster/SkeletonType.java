
package io.vera.meta.entity.living.monster;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum SkeletonType {

    NORMAL(0),
    WITHER(1),
    STRAY(2);

    @Getter
    private final int data;

    SkeletonType(int data) {
        this.data = data;
    }

    @Nonnull
    public static SkeletonType of(int id) {
        for (SkeletonType type : values()) {
            if (type.data == id) {
                return type;
            }
        }

        throw new IllegalArgumentException("no skeleton type with id = " + id);
    }
}