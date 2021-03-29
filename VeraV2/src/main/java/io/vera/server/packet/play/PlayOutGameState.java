package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutGameState extends PacketOut {
  private final int reason;
  
  private final float val;
  
  public PlayOutGameState(int reason, float val) {
    super(PlayOutGameState.class);
    this.reason = reason;
    this.val = val;
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.reason);
    buf.writeFloat(this.val);
  }
}
