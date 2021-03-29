package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInUseEntity extends PacketIn {
  public PlayInUseEntity() {
    super(PlayInUseEntity.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    int target = NetData.rvint(buf);
    int type = NetData.rvint(buf);
    float x = Float.NaN;
    float y = Float.NaN;
    float z = Float.NaN;
    int hand = -1;
    if (type == 2) {
      x = buf.readFloat();
      y = buf.readFloat();
      z = buf.readFloat();
    } 
    if (type == 0 || type == 2)
      hand = NetData.rvint(buf); 
  }
}
