package io.vera.server.world;

import io.vera.logger.Logger;
import io.vera.util.Misc;
import io.vera.world.opt.Dimension;
import io.vera.world.opt.WorldCreateSpec;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WorldLoader {
  private static final String DEFAULT_WORLD_NAME = "world";
  
  private static final SimpleFileVisitor<Path> DELETE_FILES = new SimpleFileVisitor<Path>() {
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }
      
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    };
  
  public static WorldLoader getInstance() {
    return instance;
  }
  
  private static final WorldLoader instance = new WorldLoader();
  
  private final Map<String, World> worlds = new ConcurrentHashMap<>();
  
  public void loadAll() {
    try {
      Files.walkFileTree(Misc.HOME_PATH, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
              Path levelDat = dir.resolve("level.dat");
              if (Files.exists(levelDat, new java.nio.file.LinkOption[0])) {
                WorldLoader.this.load(dir.getFileName().toString(), dir, Dimension.OVERWORLD);
                return FileVisitResult.SKIP_SUBTREE;
              } 
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    if (this.worlds.isEmpty() || !this.worlds.containsKey("world"))
      create("world", WorldCreateSpec.getDefaultOptions()); 
  }
  
  @Nonnull
  private World load(String name, Path enclosing, Dimension dimension) {
    Logger.get(getClass()).log("Loading world \"" + name + "\"...");
    World world = new World(name, enclosing, dimension);
    world.loadSpawnChunks();
    this.worlds.put(name, world);
    Logger.get(getClass()).log("Finished loading \"" + name + "\".");
    return world;
  }
  
  public Map<String, World> getWorlds() {
    return Collections.unmodifiableMap(this.worlds);
  }
  
  public Collection<World> worlds() {
    return this.worlds.values();
  }
  
  public World getDefaultWorld() {
    return this.worlds.get("world");
  }
  
  public World get(String name) {
    World world = this.worlds.get(name);
    if (world != null)
      return world; 
    Path enclosing = Misc.HOME_PATH.resolve(name);
    if (Files.isDirectory(enclosing, new java.nio.file.LinkOption[0])) {
      Path levelDat = enclosing.resolve("level.dat");
      if (Files.exists(levelDat, new java.nio.file.LinkOption[0]))
        return load(name, enclosing, Dimension.OVERWORLD); 
    } 
    throw new IllegalArgumentException(name + " has no world");
  }
  
  public World create(String name, WorldCreateSpec spec) {
    return this.worlds.compute(name, (k, v) -> {
          if (v != null)
            throw new IllegalArgumentException("World \"" + name + "\" already exists"); 
          Logger.get(getClass()).log("Creating world \"" + name + "\"...");
          World world = new World(name, Misc.HOME_PATH.resolve(name), spec);
          world.loadSpawnChunks();
          world.save();
          Logger.get(getClass()).log("Finished creating \"" + name + "\".");
          return world;
        });
  }
  
  public boolean delete(World world) {
    if (this.worlds.remove(world.getName()) != null) {
      Path path = world.getDirectory();
      try {
        Files.walkFileTree(path, DELETE_FILES);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
    } 
    return false;
  }
}
