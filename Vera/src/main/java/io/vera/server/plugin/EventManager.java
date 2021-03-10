
package io.vera.server.plugin;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.vera.event.annotations.EventInvoked;
import io.vera.event.annotations.Supertype;
import io.vera.event.base.DispatchOrder;
import io.vera.event.base.Event;
import io.vera.event.base.Listener;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import lombok.Getter;
import io.vera.logger.Logger;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;


@NotThreadSafe
public final class EventManager {
    private static final ServerThreadPool PLUGIN_EXECUTOR = ServerThreadPool.forSpec(PoolSpec.PLUGINS);

    @Getter
    private static final EventManager instance = new EventManager();

    private final ConcurrentMap<Class<? extends Event>, ConcurrentSkipListSet<EventDispatcher>> listeners = new ConcurrentHashMap<>();

    
    public void registerEvent(Listener listener) {
        Class<?> cls = listener.getClass();
        MethodAccess access = MethodAccess.get(cls);
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            Parameter[] params = m.getParameters();
            if (params.length == 1) {
                Parameter p = params[0];

                Class<?> pType = p.getType();
                if (pType.isAnnotationPresent(Supertype.class)) {
                    new IllegalArgumentException("Attempted to register listener for supertype: " + pType.getSimpleName()).
                            printStackTrace();
                    continue;
                }

                if (Event.class.isAssignableFrom(pType)) {
                    Class<? extends Event> clazz = (Class<? extends Event>) pType;
                    ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.computeIfAbsent(
                            clazz, k -> new ConcurrentSkipListSet<>());
                    EventInvoked opts = m.getAnnotation(EventInvoked.class);
                    DispatchOrder order = DispatchOrder.MIDDLE;
                    if (opts != null) {
                        order = opts.order();
                    }

                    EventDispatcher last;
                    if (order == DispatchOrder.LAST &&
                            !dispatchers.isEmpty() &&
                            (last = dispatchers.last()).isLast()) {
                        Logger.get("Registrar").warn("Event listener \"" +
                                m.getName() + "\" will override the last event listener in " +
                                last.getContainer().getClass().getSimpleName() + ".java");
                    }

                    dispatchers.add(new EventDispatcher(access, listener, m, order));
                }
            }
        }
    }

    
    public void unregister(Class<? extends Listener> listener) {
        for (ConcurrentSkipListSet<EventDispatcher> queue : this.listeners.values()) {
            queue.removeIf(dispatcher -> dispatcher.isContainedBy(listener));
        }
    }


    public <T extends Event> void callEvent(T event) {
        ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.get(event.getClass());
        if (dispatchers != null) {
            for (EventDispatcher dispatcher : dispatchers) {
                dispatcher.fire(event);
            }
        }
    }

    
    public <T extends Event> void callEvent(T event, Consumer<T> callback) {
        ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.get(event.getClass());
        CompletableFuture<T> future = CompletableFuture.completedFuture(event);
        if (dispatchers != null) {
            for (EventDispatcher dispatcher : dispatchers) {
                future.thenApplyAsync(dispatcher::fire, PLUGIN_EXECUTOR).exceptionally(t -> {
                    t.printStackTrace();
                    return event;
                });
            }
        }
        future.thenAcceptAsync(callback, PLUGIN_EXECUTOR);
    }
}