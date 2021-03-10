
package io.vera.server.ui.tablist;

import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;


@ThreadSafe
public class CustomTabList extends TabList {

    private static final int MAX_NAME_LENGTH = 16;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void setElement(int slot, ChatComponent value) {
        synchronized (this.lock) {
            if(value != null) {
                if (this.elements.size() > slot && this.elements.get(slot) != null) {
                    this.elements.get(slot).setDisplayName(value);

                    PlayOutTabListItem.UpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                    packet.update(this.elements.get(slot).getUuid(), value);
                    this.getUserList().forEach(player -> ((VeraPlayer) player).net().sendPacket(packet));
                } else {
                    List<TabListElement> addedElements = new ArrayList<>();

                    for (int i = 0; i < slot; i++) {
                        if (this.elements.size() == i || this.elements.get(i) == null) {
                            TabListElement blank = new TabListElement();
                            blank.setName(this.getName(i));
                            blank.setBlank(true);
                            blank.setDisplayName(ChatComponent.empty());
                            this.elements.add(i, blank);
                            addedElements.add(blank);
                        }
                    }

                    TabListElement element = new TabListElement();
                    element.setDisplayName(value);
                    element.setName(this.getName(slot));

                    this.elements.add(slot, element);
                    addedElements.add(element);

                    if (!addedElements.isEmpty()) {
                        PlayOutTabListItem.AddPlayer packet = PlayOutTabListItem.addPlayerPacket();
                        addedElements.forEach(e -> packet.addPlayer(e.getUuid(), e.getName(), e.getGameMode(), e.getPing(), e.getDisplayName()));
                        this.getUserList().forEach(player -> ((VeraPlayer) player).net().sendPacket(packet));
                    }
                }
            } else {
                if (this.elements.size() > slot && this.elements.get(slot) != null) {
                    if (slot == this.elements.size() - 1) {
                        List<TabListElement> removedElements = new ArrayList<>();

                        removedElements.add(this.elements.get(slot));
                        this.elements.remove(slot);

                        for (int i = this.elements.size() - 1; i >= 0; i--) {
                            if (this.elements.get(i).isBlank()) {
                                removedElements.add(this.elements.get(i));
                                this.elements.remove(i);
                            } else {
                                break;
                            }
                        }

                        PlayOutTabListItem.RemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
                        removedElements.forEach(e -> packet.removePlayer(e.getUuid()));
                        this.getUserList().forEach(player -> ((VeraPlayer) player).net().sendPacket(packet));
                    } else {
                        this.elements.get(slot).setDisplayName(ChatComponent.empty());
                        this.elements.get(slot).setBlank(true);

                        PlayOutTabListItem.UpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                        packet.update(this.elements.get(slot).getUuid(), ChatComponent.empty());
                        this.getUserList().forEach(player -> ((VeraPlayer) player).net().sendPacket(packet));
                    }
                }
            }
        }
    }

    public ChatComponent getElement(int slot) {
        TabListElement element;
        synchronized (this.lock) {
            element = this.elements.get(slot);
        }

        if (element == null) {
            return null;
        }

        return element.getDisplayName();
    }

    private String getName(int slot) {
        int count = slot % MAX_NAME_LENGTH + 1;
        int position = slot / MAX_NAME_LENGTH;
        return new String(new char[count]).replace("\0", String.valueOf(ALPHABET.charAt(position)));
    }
}