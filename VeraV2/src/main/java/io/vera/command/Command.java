
package io.vera.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String[] aliases() default {};

    String help() default "";

    String desc() default "";
}
