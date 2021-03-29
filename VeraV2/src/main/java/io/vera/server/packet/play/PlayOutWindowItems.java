package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.inventory.VeraInventory;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayOutWindowItems extends PacketOut {
  private final VeraInventory inventory;
  
  public PlayOutWindowItems(VeraInventory inventory) {
    super(PlayOutWindowItems.class);
    this.inventory = inventory;
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.inventory.getId());
    buf.writeShort(this.inventory.getSize());
    for (int i = 0; i < this.inventory.getSize(); i++)
      Slot.newSlot(this.inventory.get(i)).write(buf); 
  }
}
