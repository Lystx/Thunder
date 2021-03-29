package io.vera.server.ui.tablist;

import io.vera.entity.living.Player;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CustomTabList extends TabList {
  private static final int MAX_NAME_LENGTH = 16;
  
  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  
  public void setElement(int slot, ChatComponent value) {
    synchronized (this.lock) {
      if (value != null) {
        if (this.elements.size() > slot && this.elements.get(slot) != null) {
          ((TabListElement)this.elements.get(slot)).setDisplayName(value);
          PlayOutTabListItem.UpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
          packet.update(((TabListElement)this.elements.get(slot)).getUuid(), value);
          getUserList().forEach(player -> ((VeraPlayer)player).net().sendPacket((PacketOut)packet));
        } else {
          List<TabListElement> addedElements = new ArrayList<>();
          for (int i = 0; i < slot; i++) {
            if (this.elements.size() == i || this.elements.get(i) == null) {
              TabListElement blank = new TabListElement();
              blank.setName(getName(i));
              blank.setBlank(true);
              blank.setDisplayName(ChatComponent.empty());
              this.elements.add(i, blank);
              addedElements.add(blank);
            } 
          } 
          TabListElement element = new TabListElement();
          element.setDisplayName(value);
          element.setName(getName(slot));
          this.elements.add(slot, element);
          addedElements.add(element);
          if (!addedElements.isEmpty()) {
            PlayOutTabListItem.AddPlayer packet = PlayOutTabListItem.addPlayerPacket();
            addedElements.forEach(e -> packet.addPlayer(e.getUuid(), e.getName(), e.getGameMode(), e.getPing(), e.getDisplayName()));
            getUserList().forEach(player -> ((VeraPlayer)player).net().sendPacket((PacketOut)packet));
          } 
        } 
      } else if (this.elements.size() > slot && this.elements.get(slot) != null) {
        if (slot == this.elements.size() - 1) {
          List<TabListElement> removedElements = new ArrayList<>();
          removedElements.add(this.elements.get(slot));
          this.elements.remove(slot);
          for (int i = this.elements.size() - 1; i >= 0 && (
            (TabListElement)this.elements.get(i)).isBlank(); i--) {
            removedElements.add(this.elements.get(i));
            this.elements.remove(i);
          } 
          PlayOutTabListItem.RemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
          removedElements.forEach(e -> packet.removePlayer(e.getUuid()));
          getUserList().forEach(player -> ((VeraPlayer)player).net().sendPacket((PacketOut)packet));
        } else {
          ((TabListElement)this.elements.get(slot)).setDisplayName(ChatComponent.empty());
          ((TabListElement)this.elements.get(slot)).setBlank(true);
          PlayOutTabListItem.UpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
          packet.update(((TabListElement)this.elements.get(slot)).getUuid(), ChatComponent.empty());
          getUserList().forEach(player -> ((VeraPlayer)player).net().sendPacket((PacketOut)packet));
        } 
      } 
    } 
  }
  
  public ChatComponent getElement(int slot) {
    TabListElement element;
    synchronized (this.lock) {
      element = this.elements.get(slot);
    } 
    if (element == null)
      return null; 
    return element.getDisplayName();
  }
  
  private String getName(int slot) {
    int count = slot % 16 + 1;
    int position = slot / 16;
    return (new String(new char[count])).replace("\000", String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(position)));
  }
}
