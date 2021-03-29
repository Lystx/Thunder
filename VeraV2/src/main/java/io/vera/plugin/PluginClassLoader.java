package io.vera.plugin;

import io.vera.server.VeraServer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PluginClassLoader extends URLClassLoader {
    private static final Map<String, Class<?>> CLASS_MAP = new HashMap<>();

    private final Set<Class<?>> classes = new HashSet<>();

    public Set<Class<?>> getClasses() {
        return this.classes;
    }

    public PluginClassLoader(File plugin) throws MalformedURLException {
        super(new URL[] { plugin.toURI().toURL() }, VeraServer.class.getClassLoader());
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cls = CLASS_MAP.get(name);
        if (cls != null)
            return cls;
        cls = super.findClass(name);
        this.classes.add(cls);
        CLASS_MAP.put(name, cls);
        return cls;
    }

    public void close() throws IOException {
        super.close();
        for (Class<?> c : this.classes) {
            if (CLASS_MAP.remove(c.getName()) == null)
                throw new RuntimeException("Failed to cleanup after class, memory leak may occur");
        }
        this.classes.clear();
    }
}
