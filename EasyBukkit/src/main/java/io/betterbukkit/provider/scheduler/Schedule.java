package io.betterbukkit.provider.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

    long delay();

    long period() default -1L;

    boolean sync() default true;


}