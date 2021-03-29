package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.ui.chat.ChatComponent;
import io.vera.ui.chat.ChatType;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutChat extends PacketOut {
  private final ChatComponent chat;
  
  private final ChatType type;
  
  private final boolean chatColors;
  
  public ChatComponent getChat() {
    return this.chat;
  }
  
  public ChatType getType() {
    return this.type;
  }
  
  public boolean isChatColors() {
    return this.chatColors;
  }
  
  public PlayOutChat(ChatComponent chat, ChatType type, boolean chatColors) {
    super(PlayOutChat.class);
    this.chat = chat;
    this.type = type;
    this.chatColors = chatColors;
  }
  
  public void write(ByteBuf buf) {
    if (!this.chatColors) {
      NetData.wstr(buf, this.chat.stripColor().toString());
    } else {
      NetData.wstr(buf, this.chat.toString());
    } 
    buf.writeByte(this.type.ordinal());
  }
}
