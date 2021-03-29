package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.living.Player;
import io.vera.event.base.Event;
import io.vera.event.player.BlockPlaceEvent;
import io.vera.inventory.Item;
import io.vera.inventory.PlayerInventory;
import io.vera.server.VeraServer;
import io.vera.server.inventory.VeraPlayerInventory;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.Block;
import io.vera.world.opt.GameMode;
import io.vera.world.other.Position;
import io.vera.world.other.Vector;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInBlockPlace extends PacketIn {
  public PlayInBlockPlace() {
    super(PlayInBlockPlace.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    VeraPlayer player = client.getPlayer();
    Vector vector = NetData.rvec(buf);
    int faceIdx = NetData.rvint(buf);
    int hand = NetData.rvint(buf);
    float cX = buf.readFloat();
    float cY = buf.readFloat();
    float cZ = buf.readFloat();
    if (cX == -1.0F && cY == -1.0F && cZ == -1.0F) {
      System.out.println("FACE: " + faceIdx);
    } else {
      VeraPlayerInventory veraPlayerInventory = player.getInventory();
      Item item = (hand == 0) ? veraPlayerInventory.getHeldItem() : veraPlayerInventory.getOffHeldItem();
      if (!item.getSubstance().isBlock())
        return; 
      Position playerPos = player.getPosition();
      Position blockPos = vector.toPosition(player.getWorld());
      if (blockPos.distanceSquared(playerPos) > 36.0D) {
        VeraServer.getInstance().getLogger().warn("Suspicious place @ " + blockPos + " by " + player
            .getName() + " (" + player.getUuid() + ')');
        return;
      } 
      PlayInPlayerDig.DigFace face = PlayInPlayerDig.DigFace.get(faceIdx);
      switch (face) {
        case BOTTOM:
          blockPos = (Position)blockPos.subtract(0, 1, 0);
          break;
        case TOP:
          blockPos = (Position)blockPos.add(0, 1, 0);
          break;
        case NORTH:
          blockPos = (Position)blockPos.subtract(0, 0, 1);
          break;
        case SOUTH:
          blockPos = (Position)blockPos.add(0, 0, 1);
          break;
        case WEST:
          blockPos = (Position)blockPos.subtract(1, 0, 0);
          break;
        case EAST:
          blockPos = (Position)blockPos.add(1, 0, 0);
          break;
        case SPECIAL:
          throw new RuntimeException("Invalid item placement");
      } 
      Block b = blockPos.getBlock();
      VeraServer.getInstance().getEventController().callEvent((Event)new BlockPlaceEvent((Player)player, b, item), e -> {
            if (!e.isCancelled()) {
              b.setSubstanceData(item.getSubstance(), item.getDamage());
              if (player.getGameMode() != GameMode.CREATIVE)
                if (hand == 0) {
                  inv.remove(36 + inv.getSelectedSlot(), 1);
                } else {
                  inv.remove(45, 1);
                }  
            } 
          });
    } 
  }
}
