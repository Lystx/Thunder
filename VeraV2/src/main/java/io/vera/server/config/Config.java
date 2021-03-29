package io.vera.server.config;

import io.vson.elements.object.VsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Config extends ConfigSection {
  private static final ConcurrentMap<Path, Config> cachedConfigs = new ConcurrentHashMap<>();
  
  private final Path path;
  
  public static void release(Path path) {
    cachedConfigs.remove(path);
  }
  
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
  
  public ConfigSection getRoot() {
    return this;
  }
  
  public ConfigSection getParent() {
    return this;
  }
  
  public void load() throws IOException {
    VsonObject object = ConfigIo.readConfig(this.path);
    read(object);
  }
  
  public void save() throws IOException {
    VsonObject object = write();
    ConfigIo.writeConfig(this.path, object);
  }
}
