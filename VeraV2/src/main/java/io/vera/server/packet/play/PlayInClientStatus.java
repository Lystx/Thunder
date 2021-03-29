package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInClientStatus extends PacketIn {
  public PlayInClientStatus() {
    super(PlayInClientStatus.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    int status = NetData.rvint(buf);
  }
}
