
package io.vera.command;

import io.vera.command.annotation.PlayerExactMatch;
import io.vera.command.annotation.PlayerFuzzyMatch;
import io.vera.command.annotation.PlayerRegexMatch;
import io.vera.entity.living.Player;
import io.vera.server.VeraServer;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public final class Transformers {

    private static final Map<Class<?>, BiFunction<String, Parameter, ?>> transformers = new ConcurrentHashMap<>();
    private static final Map<Class<?>, BiFunction<String, Parameter, ?>> inheritedTransformers = new ConcurrentHashMap<>();

    static {
        registerTransformer(byte.class, (s, p) -> {
            try {
                return Byte.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter an integer in -128 to 127!");
            }
        });
        registerTransformer(short.class, (s, p) -> {
            try {
                return Short.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter an integer in -65536 to 65535!");
            }
        });
        registerTransformer(int.class, (s, p) -> {
            try {
                return Integer.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter an integer!");
            }
        });
        registerTransformer(long.class, (s, p) -> {
            try {
                return Long.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter an integer!");
            }
        });
        registerTransformer(float.class, (s, p) -> {
            try {
                return Float.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter a number!");
            }
        });
        registerTransformer(double.class, (s, p) -> {
            try {
                return Double.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter a number!");
            }
        });
        registerTransformer(Number.class, (s, p) -> { // ? @ Nick
            try {
                return Double.valueOf(s);
            } catch (Exception ex) {
                throw new TransformationException("Invalid input! Enter a number!");
            }
        });
        registerTransformer(boolean.class, (s, p) -> !s.isEmpty() && (s.charAt(0) == 'y' || s.charAt(0) == 't'));
        registerTransformer(String.class, (s, p) -> s);
        registerTransformer(Object.class, (s, p) -> s);
        registerTransformer(Player.class, (s, p) -> {
            PlayerExactMatch pem = p != null ? p.getAnnotation(PlayerExactMatch.class) : null;
            PlayerFuzzyMatch pfm = p != null ? p.getAnnotation(PlayerFuzzyMatch.class) : null;
            PlayerRegexMatch prm = p != null ? p.getAnnotation(PlayerRegexMatch.class) : null;
            if (pem != null) {
                return Player.byName(s);
            } else if (pfm != null) {
                return Player.fuzzySearch(s).entrySet().stream().findAny().map(Map.Entry::getValue).orElse(null);
            } else if (prm != null) {
                Pattern pattern;
                try {
                    pattern = Pattern.compile(s);
                } catch (Exception ex) {
                    throw new TransformationException("Invalid regex pattern provided");
                }
                return VeraServer.getInstance().getPlayers().stream()
                        .filter(x -> pattern.matcher(x.getName()).find())
                        .findAny()
                        .orElse(null);
            } else {
                return Player.search(s).entrySet().stream().findAny().map(Map.Entry::getValue).orElse(null);
            }
        });
    }

    private Transformers() {
    }

    public static <T> void registerTransformer(Class<T> clazz, BiFunction<String, Parameter, ?> transformer) {
        Objects.requireNonNull(clazz, "class cannot be null");
        Objects.requireNonNull(transformer, "transformer for " + clazz + " cannot be null");
        if (transformers.putIfAbsent(clazz, transformer) != null)
            return;
        Class<?> cls = clazz.getSuperclass();
        while (cls != null && cls != Object.class) {
            if (inheritedTransformers.putIfAbsent(cls, transformer) != null)
                break;
            cls = cls.getSuperclass();
        }
    }

    public static <T> T transform(String input, Parameter parameter, Class<T> clazz) throws Exception {
        Objects.requireNonNull(input, "input cannot be null");
        Objects.requireNonNull(parameter, "parameter cannot be null");
        BiFunction<String, Parameter, ?> transformer = transformers.get(clazz);
        if (transformer == null)
            transformer = inheritedTransformers.get(clazz);
        if (transformer == null)
            throw new UnsupportedOperationException("transformer missing for " + clazz);
        return (T) transformer.apply(input, parameter);
    }

}
