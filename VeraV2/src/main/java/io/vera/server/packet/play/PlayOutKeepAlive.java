package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutKeepAlive extends PacketOut {
  private final NetClient client;
  
  public PlayOutKeepAlive(NetClient client) {
    super(PlayOutKeepAlive.class);
    this.client = client;
  }
  
  public void write(ByteBuf buf) {
    int query = PlayInKeepAlive.query(this.client);
    NetData.wvint(buf, query);
  }
}
