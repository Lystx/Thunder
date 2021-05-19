package io.thunder.utils;

import io.thunder.packet.Packet;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ThunderUtils {

    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
    public static <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<?> constructor;

            try {
                List<Constructor<?>> constructors = Arrays.asList(tClass.getDeclaredConstructors());

                constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                constructor = constructors.get(constructors.size() - 1);
            } catch (Exception e) {
                constructor = null;
            }


            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            T object = null;
            if (constructor != null) {
                Object[] args = new Object[constructor.getParameters().length];
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                        args[i] = -1;
                    } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                object = (T) constructor.newInstance(args);
            }

            if (object == null) {
                object = tClass.newInstance();
            }

            return object;
        } catch (Exception e) {
            return null;
        }

    }
}
