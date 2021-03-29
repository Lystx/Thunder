package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.Entity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutEntityHeadLook extends PacketOut {
  private final int id;
  
  private final float yaw;
  
  public PlayOutEntityHeadLook(Entity entity) {
    super(PlayOutEntityHeadLook.class);
    this.id = entity.getId();
    this.yaw = entity.getPosition().getYaw();
  }
  
  public void write(ByteBuf buf) {
    NetData.wvint(buf, this.id);
    buf.writeByte(NetData.convertAngle(this.yaw));
  }
}
