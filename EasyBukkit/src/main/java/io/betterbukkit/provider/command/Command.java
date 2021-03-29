package io.betterbukkit.provider.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String description() default "";

    String[] aliases() default {};

    String permission() default "";

    String permissionMessage() default "§cYou do not have the permission to execute this command!";
}

