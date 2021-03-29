
package de.lystx.messenger.networking.elements;

import de.lystx.messenger.networking.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 * {@link PacketHandler}
 * @param <T>
 */
@Getter @AllArgsConstructor
public class ObjectMethod<T> {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final T annotation;

}
