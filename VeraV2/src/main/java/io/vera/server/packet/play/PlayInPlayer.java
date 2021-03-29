package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInPlayer extends PacketIn {
  public PlayInPlayer() {
    super(PlayInPlayer.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    boolean onGround = buf.readBoolean();
    client.getPlayer().setOnGround(onGround);
  }
}
