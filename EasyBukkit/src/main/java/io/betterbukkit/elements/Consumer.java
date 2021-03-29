package io.betterbukkit.elements;

import java.util.Map;

public interface Consumer<T> {

    T consume(T t);
}
