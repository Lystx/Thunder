package net.hytora.library.manager.networking.connection.adapter;

import lombok.Getter;
import net.hytora.library.manager.networking.connection.packet.Packet;
import net.hytora.library.manager.networking.elements.ObjectMethod;
import net.hytora.library.manager.networking.packet.PacketHandler;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class PacketAdapter {

    private final Map<Object, List<ObjectMethod<PacketHandler>>> registeredClasses;

    public PacketAdapter() {
        this.registeredClasses = new HashMap<>();
    }

    public void registerAdapter(Object adapterHandler) {
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
        } catch (Exception ignored) {
        }
    }
}
