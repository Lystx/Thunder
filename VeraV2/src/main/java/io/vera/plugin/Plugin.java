package io.vera.plugin;

import io.vera.util.Misc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Plugin {
    public static final Path PLUGIN_DIR = Misc.HOME_PATH.resolve("plugins");

    private Path path;

    private File directory;

    private PluginDesc description;

    private PluginClassLoader classLoader;

    public Path getPath() {
        return this.path;
    }

    public File getDirectory() {
        return this.directory;
    }

    public PluginDesc getDescription() {
        return this.description;
    }

    public PluginClassLoader getClassLoader() {
        return this.classLoader;
    }

    void init(Path path, PluginDesc desc, PluginClassLoader classLoader) {
        if (this.path == null) {
            this.path = path;
            this.directory = PLUGIN_DIR.resolve(desc.id()).toFile();
            this.description = desc;
            this.classLoader = classLoader;
        }
    }

    void release() {
        try {
            if (this.classLoader != null) {
                this.classLoader.close();
                this.classLoader = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {}

    public void setup() {}

    public void cleanup() {}
}
