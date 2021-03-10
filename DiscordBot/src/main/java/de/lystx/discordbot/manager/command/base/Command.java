package de.lystx.discordbot.manager.command.base;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String description() default "";

    Permission permission() default Permission.UNKNOWN;

    String[] aliases() default {};
}

