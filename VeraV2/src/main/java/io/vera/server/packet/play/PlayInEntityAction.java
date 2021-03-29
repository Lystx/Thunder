package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

public class PlayInEntityAction extends PacketIn {
  public PlayInEntityAction() {
    super(PlayInEntityAction.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    int entityId = NetData.rvint(buf);
    int actionId = NetData.rvint(buf);
    int jumpBoost = NetData.rvint(buf);
    VeraPlayer player = client.getPlayer();
    switch (actionId) {
      case 0:
        player.setCrouching(true);
        break;
      case 1:
        player.setCrouching(false);
        break;
      case 3:
        player.setSprinting(true);
        break;
      case 4:
        player.setSprinting(false);
        break;
    } 
    player.updateMetadata();
  }
}
