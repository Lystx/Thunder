package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.Entity;
import io.vera.inventory.InventoryType;
import io.vera.server.inventory.VeraInventory;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayOutOpenWindow extends PacketOut {
  private final VeraInventory inventory;
  
  private final Entity entity;
  
  public PlayOutOpenWindow(VeraInventory inventory, Entity entity) {
    super(PlayOutOpenWindow.class);
    this.inventory = inventory;
    this.entity = entity;
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.inventory.getId());
    NetData.wstr(buf, this.inventory.getType().toString());
    NetData.wstr(buf, this.inventory.getTitle().toString());
    buf.writeByte(this.inventory.getSize());
    if (this.inventory.getType() == InventoryType.HORSE)
      buf.writeInt(this.entity.getId()); 
  }
}
