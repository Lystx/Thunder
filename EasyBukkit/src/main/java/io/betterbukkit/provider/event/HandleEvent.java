
package io.betterbukkit.provider.event;



import io.betterbukkit.provider.event.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleEvent {

    Priority priority() default Priority.NORMAL;

}
