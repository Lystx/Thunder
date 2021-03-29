package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.world.vector.AbstractVector;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutLightning extends PacketOut {
  private final double x;
  
  private final double y;
  
  private final double z;
  
  public PlayOutLightning(AbstractVector<?> vector) {
    super(PlayOutLightning.class);
    this.x = vector.getX();
    this.y = vector.getY();
    this.z = vector.getZ();
  }
  
  public void write(ByteBuf buf) {
    NetData.wvint(buf, VeraEntity.EID_COUNTER.incrementAndGet());
    buf.writeByte(1);
    buf.writeDouble(this.x);
    buf.writeDouble(this.y);
    buf.writeDouble(this.z);
  }
}
