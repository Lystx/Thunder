package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.server.player.VeraPlayerMeta;
import io.vera.ui.chat.ClientChatMode;

public class PlayInClientSettings extends PacketIn {
  public PlayInClientSettings() {
    super(PlayInClientSettings.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    String locale = NetData.rstr(buf);
    byte renderDistance = buf.readByte();
    ClientChatMode chatMode = ClientChatMode.of(NetData.rvint(buf));
    boolean chatColors = buf.readBoolean();
    byte skinFlags = buf.readByte();
    int mainHand = buf.readByte();
    VeraPlayer player = client.getPlayer();
    VeraPlayerMeta metadata = player.getMetadata();
    player.setRenderDistance(renderDistance);
    player.setLocale(locale);
    player.setChatColors(chatColors);
    player.setChatMode(chatMode);
    metadata.setSkinFlags(skinFlags);
    metadata.setLeftHandMain((mainHand == 0));
    player.updateMetadata();
  }
}
