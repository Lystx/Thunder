
package io.vera.command;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import io.vera.doc.Policy;
import io.vera.logger.Logger;
import io.vera.plugin.Plugin;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandHandler {

    public static <T> void registerTransformer(Class<T> clazz, BiFunction<String, Parameter, ?> transformer) {
        Transformers.registerTransformer(clazz, transformer);
    }

    public static <T> T transform(String input, Parameter parameter, Class<T> clazz) throws Exception {
        return Transformers.transform(input, parameter, clazz);
    }

    private static final AtomicInteger init = new AtomicInteger();
    private final Map<Predicate<Method>, Function<Method, CommandDispatcher>> dispatcherProviders = new LinkedHashMap<>();
    private final Map<String, CommandDispatcher> dispatchers = new ConcurrentHashMap<>();
    private final Map<String, Map<String, CommandDispatcher>> pluginDispatchers = new ConcurrentHashMap<>();

    @Getter
    private int cmdCount;

    public CommandHandler() {
        if (!init.compareAndSet(0, 1)) {
            throw new IllegalStateException("Use Server#getCmdHandler()");
        }
    }

    public void register(Plugin plugin, CommandListener listener) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");

        Class<? extends CommandListener> cls = listener.getClass();
        MethodAccess access = MethodAccess.get(cls);
        Method[] methods = cls.getDeclaredMethods();

        String fallback = plugin.getDescription().id().toLowerCase();

        for (Method method : methods) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();

            String methodSignature = getMethodSignature(method);

            String name = cmd.name().toLowerCase();
            if (!isValidCommandName(name)) {
                Logger.get(CommandHandler.class).error("Error registering command \"" + name + "\" from " + methodSignature + ": command may not have a space in it");
                continue;
            }

            CommandDispatcher dispatcher = null;
            if (parameterTypes.length >= 2 && parameterTypes[0] == CommandSource.class && parameterTypes[1] == String[].class) {
                try {
                    dispatcher = new ParamsCommandDispatcher(access, listener, method, fallback, cmd);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    Logger.get(CommandHandler.class).error("Error registering command \"" + name + "\" from " + methodSignature + ": " + ex.getMessage());
                    continue;
                }
            } else {
                for (Map.Entry<Predicate<Method>, Function<Method, CommandDispatcher>> provider : this.dispatcherProviders.entrySet()) {
                    if (provider.getKey().test(method)) {
                        dispatcher = provider.getValue().apply(method);
                        if (dispatcher != null)
                            break;
                    }
                }
                if (dispatcher == null)
                    Logger.get(CommandHandler.class).error("Error registering command \"" + name + "\" from " + methodSignature + ": does not match any expected method signature");
                continue;
            }

            CommandDispatcher oldDispatcher = this.dispatchers.put(name, dispatcher);
            CommandDispatcher newDispatcher = dispatcher;
            if (oldDispatcher != null) {
                Logger.get(CommandHandler.class).warn("Overwriting old /" + name + " from " + oldDispatcher.getPlugin() + " with new handler from " + dispatcher.getPlugin());
            }
            this.pluginDispatchers.compute(fallback, (x, m) -> {
                if (m == null)
                    m = new ConcurrentHashMap<>();
                m.put(name, newDispatcher);
                return m;
            });
            for (String _alias : cmd.aliases()) {
                final String alias = _alias.toLowerCase();
                if (!isValidCommandName(alias)) {
                    Logger.get(CommandHandler.class).error("Error registering command \"" + alias + "\" from " + methodSignature + ": alias may not have a space in it");
                    continue;
                }
                oldDispatcher = this.dispatchers.put(alias, dispatcher);
                if (oldDispatcher != null) {
                    Logger.get(CommandHandler.class).warn("Overwriting old /" + alias + " from " + oldDispatcher.getPlugin() + " with new handler from " + dispatcher.getPlugin());
                }
                this.pluginDispatchers.compute(fallback, (x, m) -> {
                    if (m == null)
                        m = new ConcurrentHashMap<>();
                    m.put(alias, newDispatcher);
                    return m;
                });
            }
            this.cmdCount++;
        }
    }

    public static String getMethodSignature(Method method) {
        String methodSignature = method.getDeclaringClass().getName() + "#" + method.getName() + "(";
        boolean first = true;
        for (Class<?> param : method.getParameterTypes()) {
            if (!first)
                methodSignature += ", ";
            methodSignature += getClassName(param);
            first = false;
        }
        methodSignature += ")";
        return methodSignature;
    }

    private static String getClassName(Class<?> cls) {
        if (cls.isArray())
            return getClassName(cls.getComponentType()) + "[]";
        return cls.getSimpleName();
    }

    private static boolean isValidCommandName(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    public void unregister(Class<? extends CommandListener> listener) {
        boolean removed = this.dispatchers.entrySet().removeIf(e -> e.getValue().isContainedBy(listener));
        if (removed) {
            this.cmdCount--;
        }
    }

    public boolean dispatch(String cmd, CommandSource source) {
        String[] split = cmd.split("\\s+");
        String label = split[0].toLowerCase();
        CommandDispatcher dispatcher;
        int colon;
        if ((colon = label.indexOf(':')) >= 0) {
            String plugin = cmd.substring(0, colon);
            label = cmd.substring(colon + 1);
            dispatcher = this.pluginDispatchers.getOrDefault(plugin, Collections.emptyMap()).get(label);
        } else {
            dispatcher = this.dispatchers.get(label);
        }
        if (dispatcher == null) {
            return false;
        }

        String[] args = new String[split.length - 1];
        if (args.length > 0) {
            System.arraycopy(split, 1, args, 0, args.length);
        }

        try {
            dispatcher.run(split[0], source, args);
        } catch (Exception ex) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("An error occurred while executing the command."));
            throw new RuntimeException(ex);
        }
        return true;
    }

    public Map<String, CommandDispatcher> getDispatchers() {
        return Collections.unmodifiableMap(this.dispatchers);
    }

    public Map<String, Map<String, CommandDispatcher>> getPluginDispatchers() {
        return Collections.unmodifiableMap(this.pluginDispatchers);
    }

    public void addDispatcherProvider(Predicate<Method> predicate, Function<Method, CommandDispatcher> function) {
        Objects.requireNonNull(predicate, "predicate cannot be null");
        Objects.requireNonNull(function, "function cannot be null");

        this.dispatcherProviders.put(predicate, function);
    }

}
