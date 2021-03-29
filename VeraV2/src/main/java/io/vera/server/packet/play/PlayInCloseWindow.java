package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.inventory.VeraInventory;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInCloseWindow extends PacketIn {
  public PlayInCloseWindow() {
    super(PlayInCloseWindow.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    short windowId = buf.readUnsignedByte();
    if (windowId == 0)
      return; 
    VeraInventory.close(windowId, client.getPlayer());
  }
}
