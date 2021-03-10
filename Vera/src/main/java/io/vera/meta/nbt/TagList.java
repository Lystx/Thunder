package io.vera.meta.nbt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;

@NotThreadSafe
@RequiredArgsConstructor
public class TagList<E> extends ArrayList<E> {
    @Getter
    private final Tag.Type type;
}
