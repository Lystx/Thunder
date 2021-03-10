
package io.vera.server.config;


import io.vson.elements.object.VsonObject;


import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Immutable
public class Config extends ConfigSection {

    private static final ConcurrentMap<Path, Config> cachedConfigs = new ConcurrentHashMap<>();

    public static void release(Path path) {
        cachedConfigs.remove(path);
    }

    private final Path path;

    protected Config(Path path) {
        super("", null, null);
        this.path = path;
    }

    public static Config load(Path path) {
        return cachedConfigs.computeIfAbsent(path, k -> {
            Config config = new Config(path);
            try {
                config.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return config;
        });
    }


    public File getFile() {
        return this.path.toFile();
    }

    public Path getPath() {
        return this.path;
    }

    public File getDirectory() {
        return this.path.getParent().toFile();
    }

    @Override
    public ConfigSection getRoot() {
        return this;
    }

    @Override
    public ConfigSection getParent() {
        return this;
    }

    public void load() throws IOException {
        VsonObject object = ConfigIo.readConfig(this.path);
        this.read(object);
    }

    public void save() throws IOException {
        VsonObject object = this.write();
        ConfigIo.writeConfig(this.path, object);
    }
}
