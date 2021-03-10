
package io.vera.plugin;

import java.lang.annotation.*;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginDesc {

    String id();

    String name() default "";

    String version() default "1.0";

    String author() default "";

    String[] depends() default {};
}