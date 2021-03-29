package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInChat extends PacketIn {
  public PlayInChat() {
    super(PlayInChat.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    VeraPlayer player = client.getPlayer();
    String msg = NetData.rstr(buf);
    if (msg.startsWith("/")) {
      ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> player.runCommand(msg.replaceFirst("/", "")));
    } else {
      player.chat(msg);
    } 
  }
}
