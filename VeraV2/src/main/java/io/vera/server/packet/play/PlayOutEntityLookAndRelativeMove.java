package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.Entity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.world.other.Position;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutEntityLookAndRelativeMove extends PacketOut {
  private final int id;
  
  private final double yaw;
  
  private final double pitch;
  
  private final boolean onGround;
  
  private final Position delta;
  
  public PlayOutEntityLookAndRelativeMove(Entity entity, Position delta) {
    super(PlayOutEntityLookAndRelativeMove.class);
    this.id = entity.getId();
    this.yaw = entity.getPosition().getYaw();
    this.pitch = entity.getPosition().getPitch();
    this.onGround = entity.isOnGround();
    this.delta = (Position)delta.multiply(4096, 4096, 4096);
  }
  
  public void write(ByteBuf buf) {
    NetData.wvint(buf, this.id);
    buf.writeShort((int)this.delta.getX());
    buf.writeShort((int)this.delta.getY());
    buf.writeShort((int)this.delta.getZ());
    buf.writeByte((int)(this.yaw * 256.0D / 360.0D));
    buf.writeByte((int)(this.pitch / 1.4D));
    buf.writeBoolean(this.onGround);
  }
}
