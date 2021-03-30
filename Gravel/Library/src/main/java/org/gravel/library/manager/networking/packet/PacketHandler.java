package org.gravel.library.manager.networking.packet;

import org.gravel.library.manager.networking.elements.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PacketHandler {

    Priority value() default Priority.NORMAL;
}
