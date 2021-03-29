package io.vera.event.annotations;

import io.vera.event.base.DispatchOrder;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface EventInvoked {
    DispatchOrder order() default DispatchOrder.MIDDLE;
}
