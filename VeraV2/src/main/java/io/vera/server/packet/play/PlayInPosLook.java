package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.world.other.Position;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInPosLook extends PacketIn {
  public PlayInPosLook() {
    super(PlayInPosLook.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    VeraPlayer player = client.getPlayer();
    double x = buf.readDouble();
    double y = buf.readDouble();
    double z = buf.readDouble();
    float yaw = buf.readFloat();
    float pitch = buf.readFloat();
    boolean isOnGround = buf.readBoolean();
    player.setPosition(new Position(player.getWorld(), x, y, z, yaw, pitch), false);
    player.setOnGround(isOnGround);
  }
}