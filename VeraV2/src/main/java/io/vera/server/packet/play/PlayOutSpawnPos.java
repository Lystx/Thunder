package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.world.other.Vector;
import io.vera.world.vector.AbstractVector;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutSpawnPos extends PacketOut {
  public static final Vector VEC = new Vector();
  
  public PlayOutSpawnPos() {
    super(PlayOutSpawnPos.class);
  }
  
  public void write(ByteBuf buf) {
    NetData.wvec(buf, (AbstractVector)VEC);
  }
}
