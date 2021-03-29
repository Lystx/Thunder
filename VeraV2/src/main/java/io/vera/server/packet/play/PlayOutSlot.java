package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayOutSlot extends PacketOut {
  private final int window;
  
  private final int pos;
  
  private final Slot slot;
  
  public PlayOutSlot(int window, int pos, Slot slot) {
    super(PlayOutSlot.class);
    this.window = window;
    this.pos = pos;
    this.slot = slot;
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.window);
    buf.writeShort(this.pos);
    this.slot.write(buf);
  }
}
