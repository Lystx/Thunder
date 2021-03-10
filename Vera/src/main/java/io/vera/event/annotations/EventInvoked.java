
package io.vera.event.annotations;

import io.vera.event.base.DispatchOrder;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface EventInvoked {

    DispatchOrder order() default DispatchOrder.MIDDLE;
}