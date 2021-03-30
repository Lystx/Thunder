package org.gravel.library.manager.networking.connection.adapter;

import lombok.Getter;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.networking.elements.ObjectMethod;
import org.gravel.library.manager.networking.packet.PacketHandler;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class PacketAdapter {

    private final List<PacketHandlerAdapter> registeredHandlers;
    private final Map<Object, List<ObjectMethod<PacketHandler>>> registeredClasses;

    public PacketAdapter() {
        this.registeredClasses = new HashMap<>();
        this.registeredHandlers = new LinkedList<>();
    }

    public void registerAdapter(Object adapterHandler) {
        if (adapterHandler instanceof PacketHandlerAdapter) {
            this.registeredHandlers.add((PacketHandlerAdapter) adapterHandler);
        }
        List<ObjectMethod<PacketHandler>> packetMethods = new LinkedList<>();

        for (Method m : adapterHandler.getClass().getDeclaredMethods()) {
            PacketHandler annotation = m.getAnnotation(PacketHandler.class);
            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];
                m.setAccessible(true);
                packetMethods.add(new ObjectMethod<>(adapterHandler, m, parameterType, annotation));
            }
        }
        packetMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        this.registeredClasses.put(adapterHandler, packetMethods);
    }

    public void unregisterAdapter(Object adapterHandler) {
        if (adapterHandler instanceof PacketHandlerAdapter) {
            this.registeredHandlers.remove(adapterHandler);
        }
        this.registeredClasses.remove(adapterHandler);
    }

    public void handelAdapterHandler(Packet packet) {
        try {
            this.registeredClasses.forEach((object, methodList) -> {
                for (ObjectMethod<PacketHandler> em : methodList) {
                    if (em.getEvent().equals(packet.getClass()))
                        try {
                            em.getMethod().invoke(em.getInstance(), packet);
                        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
                            e.printStackTrace();
                        }
                }
            });
            for (PacketHandlerAdapter adapter : this.registeredHandlers) {
                adapter.handle(packet);
            }
        } catch (Exception ignored) {
        }
    }
}
