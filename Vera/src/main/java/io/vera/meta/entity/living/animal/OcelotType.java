
package io.vera.meta.entity.living.animal;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum OcelotType {

    WILD(0),

    TUXEDO(1),
    TABBY(2),
    SIAMESE(3);

    @Getter
    private final int data;

    OcelotType(int data) {
        this.data = data;
    }

    @Nonnull
    public static OcelotType of(int id) {
        for (OcelotType type : values()) {
            if (type.data == id) {
                return type;
            }
        }

        throw new IllegalArgumentException("no ocelot type with id = " + id);
    }
}