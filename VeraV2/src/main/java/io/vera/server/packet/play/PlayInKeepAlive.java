package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.util.Cache;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInKeepAlive extends PacketIn {
  private static final Cache<NetClient, Integer> TICK_IDS;
  
  static {
    TICK_IDS = new Cache(30000L, (client, id) -> {
          client.disconnect("No KeepAlive response");
          return Boolean.valueOf(true);
        });
  }
  
  public static int query(NetClient client) {
    return ((Integer)TICK_IDS.compute(client, (k, v) -> {
          if (v == null)
            return Integer.valueOf(ThreadLocalRandom.current().nextInt(268435455)); 
          client.disconnect("No KeepAlive response");
          return null;
        })).intValue();
  }
  
  public PlayInKeepAlive() {
    super(PlayInKeepAlive.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    int id = NetData.rvint(buf);
    Integer localId = (Integer)TICK_IDS.get(client);
    if (localId != null && id != localId.intValue()) {
      client.disconnect("Keep alive ID mismatch, actual:" + localId + " rcvd:" + id);
      return;
    } 
    if (System.nanoTime() - client.lastKeepAlive() > 30000000000L) {
      client.disconnect("Timed out");
      return;
    } 
    TICK_IDS.compute(client, (k, v) -> null);
  }
}
