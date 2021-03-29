package io.vera.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginDesc {
    String id();

    String name() default "";

    String version() default "1.0";

    String author() default "";

    String[] depends() default {};
}
