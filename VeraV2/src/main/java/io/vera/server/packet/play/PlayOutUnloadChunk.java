package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayOutUnloadChunk extends PacketOut {
  private final int x;
  
  private final int z;
  
  public PlayOutUnloadChunk(int x, int z) {
    super(PlayOutUnloadChunk.class);
    this.x = x;
    this.z = z;
  }
  
  public void write(ByteBuf buf) {
    buf.writeInt(this.x);
    buf.writeInt(this.z);
  }
}
