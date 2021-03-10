
package io.vera.server.plugin;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.vera.event.base.DispatchOrder;
import io.vera.event.base.Event;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;
import java.lang.reflect.Method;


@Immutable
public final class EventDispatcher implements Comparable<EventDispatcher> {
    private final MethodAccess access;
    @Getter
    private final Object container;
    private final int idx;
    private final DispatchOrder order;

    public EventDispatcher(MethodAccess access, Object inst, Method method, DispatchOrder order) {
        this.access = access;
        this.container = inst;
        this.order = order;

        this.idx = access.getIndex(method.getName(), method.getParameterTypes());
    }


    public Event fire(Event event) {
        this.access.invoke(this.container, this.idx, event);
        return event;
    }

    public boolean isContainedBy(Class<?> cls) {
        return this.container.getClass().equals(cls);
    }

    public boolean isLast() {
        return this.order == DispatchOrder.LAST;
    }

    @Override
    public int compareTo(EventDispatcher o) {
        if (this.order == o.order) {
            return 1;
        }

        return this.order.compareTo(o.order);
    }
}