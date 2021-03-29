package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutPluginMsg extends PacketOut {
  public static final PlayOutPluginMsg BRAND = new PlayOutPluginMsg("MC|Brand", "tridentsdk"
      .getBytes(NetData.NET_CHARSET));
  
  private final String channel;
  
  private final byte[] data;
  
  public PlayOutPluginMsg(String channel, byte[] data) {
    super(PlayOutPluginMsg.class);
    if (data.length >= 32767)
      throw new ArrayIndexOutOfBoundsException("Data must have len < Short.MAX_VALUE"); 
    this.channel = channel;
    this.data = data;
  }
  
  public void write(ByteBuf buf) {
    NetData.wstr(buf, this.channel);
    buf.writeBytes(this.data);
  }
}
