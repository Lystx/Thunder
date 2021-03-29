package io.vera.server.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.vera.server.VeraServer;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.login.LoginOutCompression;
import io.vera.server.packet.login.LoginOutDisconnect;
import io.vera.server.packet.play.PlayOutDisconnect;
import io.vera.server.packet.play.PlayOutKeepAlive;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class NetClient {
  public static final int BUFFER_SIZE = 8192;
  
  public static final long KEEP_ALIVE_KICK_NANOS = 30000000000L;
  
  private static final long KEEP_ALIVE_INTERVAL_NANOS = 10000000000L;
  
  public enum NetState {
    HANDSHAKE, STATUS, LOGIN, PLAY;
  }
  
  private static final Map<SocketAddress, NetClient> CLIENTS = new ConcurrentHashMap<>();
  
  private final Channel channel;
  
  private volatile NetState state;
  
  public Channel getChannel() {
    return this.channel;
  }
  
  public void setState(NetState state) {
    this.state = state;
  }
  
  public NetState getState() {
    return this.state;
  }
  
  private final AtomicReference<String> name = new AtomicReference<>();
  
  private volatile NetCrypto cryptoModule;
  
  private volatile boolean doCompression;
  
  private volatile VeraPlayer player;
  
  public NetCrypto getCryptoModule() {
    return this.cryptoModule;
  }
  
  public VeraPlayer getPlayer() {
    return this.player;
  }
  
  public void setPlayer(VeraPlayer player) {
    this.player = player;
  }
  
  private final AtomicLong ping = new AtomicLong();
  
  public AtomicLong getPing() {
    return this.ping;
  }
  
  private final AtomicLong lastKeepAlive = new AtomicLong(System.nanoTime());
  
  public NetClient(ChannelHandlerContext ctx) {
    this.channel = ctx.channel();
    this.state = NetState.HANDSHAKE;
    this.channel.closeFuture().addListener((GenericFutureListener)new ChannelFutureListener() {
          public void operationComplete(ChannelFuture channelFuture) throws Exception {
            channelFuture.removeListener((GenericFutureListener)this);
            NetClient.this.disconnect("Player lost connection");
          }
        });
  }
  
  public static NetClient get(ChannelHandlerContext ctx) {
    return CLIENTS.computeIfAbsent(ctx.channel().remoteAddress(), k -> new NetClient(ctx));
  }
  
  public long lastKeepAlive() {
    return this.lastKeepAlive.get();
  }
  
  public void tick() {
    long lastKeepAlive = this.lastKeepAlive.get();
    long now = System.nanoTime();
    long elapsed = now - lastKeepAlive;
    if (elapsed > 10000000000L && 
      this.lastKeepAlive.compareAndSet(lastKeepAlive, now))
      sendPacket((PacketOut)new PlayOutKeepAlive(this)); 
  }
  
  public NetCrypto initCrypto() {
    return this.cryptoModule = new NetCrypto();
  }
  
  public boolean doCompression() {
    return this.doCompression;
  }
  
  public void enableCompression() {
    if (VeraServer.cfg().compressionThresh() != -1)
      sendPacket((PacketOut)new LoginOutCompression())
        .addListener(future -> this.doCompression = true); 
  }
  
  public ChannelFuture sendPacket(PacketOut packet) {
    return this.channel.writeAndFlush(packet);
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public void setName(String name) {
    this.name.set(name);
  }
  
  public void disconnect(String reason) {
    disconnect(ChatComponent.text(reason));
  }
  
  public Future<Void> disconnect(ChatComponent reason) {
    ChannelFuture channelFuture;
    String name = this.name.get();
    if (name == null) {
      this.channel.close();
      CLIENTS.remove(this.channel.remoteAddress());
      return null;
    } 
    if (name.equals("safe_name_overrite_not_from_minecraft_possible"))
      return null; 
    Future<Void> waiter = null;
    if (this.name.compareAndSet(name, "safe_name_overrite_not_from_minecraft_possible")) {
      NetState state = this.state;
      if (state == NetState.LOGIN) {
        channelFuture = sendPacket((PacketOut)new LoginOutDisconnect(reason)).addListener(future -> {
              this.channel.close();
              VeraServer.getInstance().getLogger().log("Player " + name + " has disconnected: " + reason.getText());
            });
      } else if (state == NetState.PLAY) {
        VeraPlayer player = this.player;
        if (player != null)
          channelFuture = sendPacket((PacketOut)new PlayOutDisconnect(reason)).addListener(future -> {
                this.channel.close();
                player.remove();
                VeraServer.getInstance().getLogger().log("Player " + name + " [" + player.getUuid() + "] has disconnected: " + reason.getText());
              }); 
      } else if (state == NetState.STATUS) {
        channelFuture = this.channel.close();
      } 
      CLIENTS.remove(this.channel.remoteAddress());
    } 
    return (Future<Void>)channelFuture;
  }
}
