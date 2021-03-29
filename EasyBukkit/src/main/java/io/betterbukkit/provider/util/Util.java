package io.betterbukkit.provider.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public abstract class Util<NEW, OLD> {

    private static final List<Util<?, ?>> utils = new LinkedList<>();

    public static void registerUtil(Util<?, ?> util) {
        utils.add(util);
    }

    public abstract NEW from(OLD old);

    public abstract OLD to(NEW aNew);

    public static <T, D> Util<T, D> get(Class<? extends Util<T, D>> tClass) {
        for (Util<?, ?> util : utils) {
            if (util.getClass().equals(tClass)) {
                return (Util<T, D>) util;
            }
        }
        return null;
    }

    public static List<String> toString(List<?> list) {
        List<String> strings = new LinkedList<>();
        list.forEach(o -> strings.add(o.toString()));
        return strings;
    }

    public static List<String> toString(List<?> list, String... methodNames) {
        List<String> strings = new LinkedList<>();
        list.forEach(o -> {
            try {
                Method finalMethod = null;
                for (String methodName : methodNames) {
                    finalMethod = o.getClass().getDeclaredMethod(methodName);
                }
                if (finalMethod != null) {
                    strings.add((String) finalMethod.invoke(o));
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
        return strings;
    }
}
