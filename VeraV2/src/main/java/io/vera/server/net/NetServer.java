package io.vera.server.net;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class NetServer {
  private final String ip;
  
  private final int port;
  
  NetServer(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }
  
  public static NetServer init(String ip, int port, boolean useNative) {
    boolean nativeCompat = System.getProperty("os.name").toLowerCase().contains("linux");
    return (nativeCompat && useNative) ? new NetEpollServer(ip, port) : new NetNioServer(ip, port);
  }
  
  public abstract void setup();
  
  public abstract void shutdown() throws InterruptedException;
  
  public String ip() {
    return this.ip;
  }
  
  public int port() {
    return this.port;
  }
}
