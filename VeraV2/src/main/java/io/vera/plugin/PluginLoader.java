package io.vera.plugin;

import io.vera.command.CommandListener;
import io.vera.event.base.Listener;
import io.vera.logger.Logger;
import io.vera.server.VeraServer;
import io.vera.util.Misc;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javassist.ClassPool;
import javassist.CtClass;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PluginLoader {
    private static final AtomicInteger init = new AtomicInteger();

    private static final ClassPool PLUGIN_CP = new ClassPool(true);

    private final Map<String, Plugin> loaded = new HashMap<>();

    public Map<String, Plugin> getLoaded() {
        return this.loaded;
    }

    public PluginLoader() {
        if (!init.compareAndSet(0, 1))
            throw new IllegalStateException("Use Server#getPluginLoader()");
    }

    public void loadAll() {
        loadAll(new HashSet<>());
    }

    private void loadAll(final Set<Path> skip) {
        try {
            Files.walkFileTree(Misc.HOME_PATH.resolve("plugins"), new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (skip.contains(file))
                        return FileVisitResult.CONTINUE;
                    PluginLoader.this.load(file, skip);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Plugin load(Path path) {
        return load(path, new HashSet<>());
    }

    private Plugin load(Path path, Set<Path> skip) {
        skip.add(path);
        File pluginFile = path.toFile();
        Set<String> names = new HashSet<>();
        try (JarFile jarFile = new JarFile(pluginFile)) {
            PLUGIN_CP.appendClassPath(pluginFile.getPath());
            PluginDesc description = null;
            String main = null;
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                    continue;
                String name = entry.getName().replace(".class", "").replace('/', '.');
                names.add(name);
                CtClass cls = PLUGIN_CP.get(name);
                PluginDesc desc = (PluginDesc)cls.getAnnotation(PluginDesc.class);
                if (desc != null && (desc.name().equals("trident") || desc.name().equals("minecraft")))
                    throw new RuntimeException("ID has illegal name");
                if (desc != null && cls.getSuperclass().getName().equals(Plugin.class.getName())) {
                    if (description != null)
                        throw new RuntimeException("Plugin cannot have more than two plugin main classes");
                    main = name;
                    description = desc;
                }
            }
            if (main == null)
                throw new RuntimeException("Plugin does not have a main class");
            if (this.loaded.containsKey(description.id()))
                throw new RuntimeException("Plugin with ID \"" + description.name() + "\" has already been loaded");
            Logger.get(PluginLoader.class).log("Loading " + description.id() + ':' + description.version() + "... ");
            if ((description.depends()).length > 0) {
                loadAll(skip);
                for (String dep : description.depends()) {
                    String[] split = dep.split(":");
                    Plugin dependency = this.loaded.get(split[0]);
                    if (dependency == null || !dependency.getDescription().version().equals(split[1]))
                        throw new RuntimeException("Dependency " + dep + " not satisfied");
                }
            }
            PluginClassLoader loader = new PluginClassLoader(pluginFile);
            Class<? extends Plugin> pluginClass = null;
            List<Class<? extends CommandListener>> commandClasses = new ArrayList<>();
            for (String name : names) {
                Class<?> cls = loader.loadClass(name);
                if (name.equals(main))
                    pluginClass = cls.asSubclass(Plugin.class);
                if (cls.isAssignableFrom(Listener.class))
                    VeraServer.getInstance().getEventController().registerEvent(cls.<Listener>asSubclass(Listener.class).getConstructor(new Class[0]).newInstance(new Object[0]));
                if (cls.isAssignableFrom(CommandListener.class))
                    commandClasses.add(cls.asSubclass(CommandListener.class));
            }
            Plugin plugin = pluginClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            plugin.init(path, description, loader);
            for (Class<? extends CommandListener> cls : commandClasses)
                VeraServer.getInstance().getCommandHandler().register(plugin, cls.<CommandListener>asSubclass(CommandListener.class).getConstructor(new Class[0]).newInstance(new Object[0]));
            this.loaded.put(description.id(), plugin);
            plugin.load();
            Logger.get(PluginLoader.class).success("Successfully loaded " + description.id() + ':' + description.version() + '!');
            return plugin;
        } catch (IOException|IllegalAccessException|InstantiationException|NoSuchMethodException|java.lang.reflect.InvocationTargetException|javassist.NotFoundException|ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unload(String plugin) {
        Plugin p = this.loaded.remove(plugin);
        if (p == null)
            return false;
        PluginDesc description = p.getDescription();
        Logger.get(PluginLoader.class).log("Unloading " + description.id() + ':' + description.version() + "... ");
        Set<Class<?>> classes = p.getClassLoader().getClasses();
        for (Class<?> cls : classes) {
            if (cls.isAssignableFrom(Listener.class))
                VeraServer.getInstance().getEventController().unregister(cls.asSubclass(Listener.class));
            if (cls.isAssignableFrom(CommandListener.class))
                VeraServer.getInstance().getCommandHandler().unregister(cls.asSubclass(CommandListener.class));
        }
        p.cleanup();
        p.release();
        return true;
    }

    public boolean unloadAll() {
        for (Plugin plugin : this.loaded.values()) {
            Set<Class<?>> classes = plugin.getClassLoader().getClasses();
            for (Class<?> cls : classes) {
                if (cls.isAssignableFrom(Listener.class))
                    VeraServer.getInstance().getEventController().unregister(cls.asSubclass(Listener.class));
                if (cls.isAssignableFrom(CommandListener.class))
                    VeraServer.getInstance().getCommandHandler().unregister(cls.asSubclass(CommandListener.class));
            }
            plugin.cleanup();
            plugin.release();
        }
        this.loaded.clear();
        return true;
    }

    public void reload() {
        if (unloadAll()) {
            loadAll();
            for (Plugin plugin : this.loaded.values())
                plugin.setup();
        } else {
            Logger.get(PluginLoader.class).error("Unloading plugins failed...");
        }
    }
}
