
package io.betterbukkit.provider.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter @AllArgsConstructor
public class EventMethod {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final HandleEvent annotation;

}
