package io.vera.server.config;

import io.vera.server.VeraServer;
import io.vera.server.player.VeraPlayer;
import io.vera.util.Misc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class OpsList extends Config {
  public static final Path PATH = Misc.HOME_PATH.resolve("ops.vson");
  
  private static final String OPS_KEY = "ops";
  
  public Set<UUID> getOps() {
    return this.ops;
  }
  
  private final Set<UUID> ops = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  private OpsList() {
    super(PATH);
  }
  
  public static OpsList init(boolean needsInit) throws IOException {
    OpsList list = new OpsList();
    if (needsInit)
      Files.write(list.getPath(), "{}".getBytes(), new java.nio.file.OpenOption[0]); 
    list.load();
    return list;
  }
  
  public void addOp(UUID uuid) {
    this.ops.add(uuid);
    set("ops", this.ops.stream().map(UUID::toString).collect(Collectors.toList()));
    try {
      save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    VeraServer.getInstance().getLogger().log(((VeraPlayer)VeraPlayer.getPlayers().get(uuid)).getName() + " [" + uuid + "] has been opped");
  }
  
  public void removeOp(UUID uuid) {
    this.ops.remove(uuid);
    set("ops", this.ops);
    try {
      save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    VeraServer.getInstance().getLogger().log(((VeraPlayer)VeraPlayer.getPlayers().get(uuid)).getName() + " [" + uuid + "] has been deopped");
  }
  
  public void load() throws IOException {
    super.load();
    if (hasKey("ops")) {
      getCollection("ops", new AbstractSet<String>() {
            public boolean add(String c) {
              OpsList.this.ops.add(UUID.fromString(c));
              return true;
            }
            
            public Iterator<String> iterator() {
              return null;
            }
            
            public int size() {
              return 0;
            }
          });
    } else {
      set("ops", this.ops);
      save();
    } 
  }
}
