package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.living.Player;
import io.vera.event.base.Event;
import io.vera.event.player.PlayerInteractEvent;
import io.vera.inventory.Item;
import io.vera.server.VeraServer;
import io.vera.server.inventory.VeraPlayerInventory;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInUseItem extends PacketIn {
  public PlayInUseItem() {
    super(PlayInUseItem.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    VeraPlayer player = client.getPlayer();
    VeraPlayerInventory veraPlayerInventory = player.getInventory();
    Item cur = (NetData.rvint(buf) == 0) ? veraPlayerInventory.getHeldItem() : veraPlayerInventory.getOffHeldItem();
    VeraServer.getInstance().getEventController().callEvent((Event)new PlayerInteractEvent((Player)player), e -> {
          if (!e.isCancelled());
        });
  }
}
