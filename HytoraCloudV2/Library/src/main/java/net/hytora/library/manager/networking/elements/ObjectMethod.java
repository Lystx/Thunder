package net.hytora.library.manager.networking.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.lang.reflect.Method;

@Getter @AllArgsConstructor
public class ObjectMethod<T> {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final T annotation;

}
