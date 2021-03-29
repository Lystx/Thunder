package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInSetSlot extends PacketIn {
  public PlayInSetSlot() {
    super(PlayInSetSlot.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    client.getPlayer().getInventory().setSelectedSlot(buf.readShort());
  }
}
