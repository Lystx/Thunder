package io.vera.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;
import javax.annotation.concurrent.Immutable;

@Immutable @Getter @AllArgsConstructor
public class Tuple<T, M> {

    private final T a;
    private final M b;

}
