
package io.betterbukkit.provider.event;

import io.betterbukkit.EasyBukkit;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class EventProvider {

    private final Map<Object, List<EventMethod>> registeredClasses;

    public EventProvider(EasyBukkit easyBukkit) {
        this.registeredClasses = new HashMap<>();
    }

    public void registerListener(EventListener listener) {
        this.registerObject(listener);
    }

    public void registerObject(Object o) {
        List<EventMethod> eventMethods = new ArrayList<>();

        for (Method m : o.getClass().getDeclaredMethods()) {
            HandleEvent annotation = m.getAnnotation(HandleEvent.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new EventMethod(o, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().priority().getValue()));
        registeredClasses.put(o, eventMethods);
    }

    public void unregister(Object instance) {
        registeredClasses.remove(instance);
    }

    public boolean callEvent(Event event) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (EventMethod em : methodList) {
                    if (em.getEvent().equals(event.getClass())) {
                        try {
                            em.getMethod().invoke(em.getInstance(), event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return event.isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}
