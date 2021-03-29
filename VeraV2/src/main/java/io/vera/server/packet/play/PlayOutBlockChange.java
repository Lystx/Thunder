package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.world.other.Position;
import io.vera.world.vector.AbstractVector;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutBlockChange extends PacketOut {
  private final Position block;
  
  private final int newBlock;
  
  public PlayOutBlockChange(Position block, int newBlock) {
    super(PlayOutBlockChange.class);
    this.block = block;
    this.newBlock = newBlock;
  }
  
  public void write(ByteBuf buf) {
    NetData.wvec(buf, (AbstractVector)this.block);
    NetData.wvint(buf, this.newBlock);
  }
}
