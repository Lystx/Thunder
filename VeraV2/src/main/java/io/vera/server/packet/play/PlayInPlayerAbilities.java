package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

public class PlayInPlayerAbilities extends PacketIn {
  public PlayInPlayerAbilities() {
    super(PlayInPlayerAbilities.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    byte flags = buf.readByte();
    boolean isGod = ((flags & 0x8) != 0);
    boolean canFly = ((flags & 0x4) != 0);
    boolean isFlying = ((flags & 0x2) != 0);
    boolean isCreative = ((flags & 0x1) != 0);
    float flyingSpeed = buf.readFloat();
    float walkingSpeed = buf.readFloat();
    VeraPlayer player = client.getPlayer();
    player.setSprinting((Double.compare(walkingSpeed, 0.30000001192092896D) == 0));
    if (player.canFly()) {
      player.setFlyingSpeed(flyingSpeed);
      player.setFlying(isFlying, false);
    } else {
      player.setFlying(false, false);
    } 
    client.sendPacket(new PlayOutPlayerAbilities(player));
  }
}
