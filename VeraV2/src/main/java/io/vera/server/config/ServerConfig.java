package io.vera.server.config;

import io.vera.util.Misc;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ServerConfig extends Config {
  public static final Path PATH = Misc.HOME_PATH.resolve("server.vson");
  
  private volatile String ip;
  
  private volatile int port;
  
  private volatile boolean useNative;
  
  private volatile int compressionThresh;
  
  private volatile boolean useAuth;
  
  private volatile int maxPlayers;
  
  private volatile String motd;
  
  private volatile boolean nettyLeakDetectorEnabled;
  
  public boolean isNettyLeakDetectorEnabled() {
    return this.nettyLeakDetectorEnabled;
  }
  
  public ServerConfig() {
    super(PATH);
  }
  
  public static ServerConfig init() throws IOException {
    ServerConfig config = new ServerConfig();
    config.load();
    return config;
  }
  
  public String ip() {
    return this.ip;
  }
  
  public int port() {
    return this.port;
  }
  
  public boolean useNative() {
    return this.useNative;
  }
  
  public int compressionThresh() {
    return this.compressionThresh;
  }
  
  public boolean doAuth() {
    return this.useAuth;
  }
  
  public int maxPlayers() {
    return this.maxPlayers;
  }
  
  public String motd() {
    return this.motd;
  }
  
  public void load() throws IOException {
    super.load();
    this.ip = getString("address");
    this.port = getInt("port");
    this.useNative = getBoolean("use-native");
    this.compressionThresh = getInt("compression-threshold");
    this.useAuth = getBoolean("online-mode");
    this.maxPlayers = getInt("max-players");
    this.motd = getString("motd");
    this.nettyLeakDetectorEnabled = getBoolean("netty-leak-detector");
  }
}
