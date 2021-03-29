package io.vera.server.ui.tablist;

import io.vera.entity.living.Player;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import java.util.Comparator;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class GlobalTabList extends TabList {
  public static GlobalTabList getInstance() {
    return instance;
  }
  
  private static final GlobalTabList instance = new GlobalTabList();
  
  public void setElement(int slot, ChatComponent value) {
    throw new RuntimeException("Attempted to edit global tablist!");
  }
  
  public ChatComponent getElement(int slot) {
    throw new RuntimeException("Attempted to grab element on global tablist");
  }
  
  public void subscribe(Player player) {
    this.users.add((VeraPlayer)player);
    PlayOutTabListItem.RemovePlayer removeAll = PlayOutTabListItem.removePlayerPacket();
    PlayOutTabListItem.AddPlayer addAll = PlayOutTabListItem.addPlayerPacket();
    synchronized (this.lock) {
      for (TabListElement element : this.elements)
        removeAll.removePlayer(element.getUuid()); 
      this.elements.clear();
      VeraPlayer.getPlayers().values()
        .stream()
        .sorted(Comparator.comparing(p -> p.getTabListName().getText()))
        .forEach(p -> {
            TabListElement e = new TabListElement(p);
            this.elements.add(e);
            addAll.addPlayer(e);
          });
    } 
    for (VeraPlayer p : this.users) {
      p.net().sendPacket((PacketOut)removeAll);
      p.net().sendPacket((PacketOut)addAll);
    } 
  }
  
  public void unsubscribe(Player player) {
    super.unsubscribe(player);
  }
}
