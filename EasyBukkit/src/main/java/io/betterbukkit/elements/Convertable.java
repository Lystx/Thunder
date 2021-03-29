package io.betterbukkit.elements;

import java.util.Map;

public interface Convertable<T> {


    Map<String, Object> convert();

    T deconvert(Map<String, Object> map);
}
