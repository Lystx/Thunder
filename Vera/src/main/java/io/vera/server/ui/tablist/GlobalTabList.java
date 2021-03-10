
package io.vera.server.ui.tablist;

import lombok.Getter;
import io.vera.entity.living.Player;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;

@ThreadSafe
public class GlobalTabList extends TabList {

    @Getter
    private static final GlobalTabList instance = new GlobalTabList();

    private GlobalTabList() {
    }

    public void setElement(int slot, ChatComponent value) {
        throw new RuntimeException("Attempted to edit global tablist!");
    }

    public ChatComponent getElement(int slot) {
        throw new RuntimeException("Attempted to grab element on global tablist");
    }

    @Override
    public void subscribe(Player player) {
        this.users.add((VeraPlayer) player);

        PlayOutTabListItem.RemovePlayer removeAll = PlayOutTabListItem.removePlayerPacket();
        PlayOutTabListItem.AddPlayer addAll = PlayOutTabListItem.addPlayerPacket();
        synchronized (this.lock) {
            for (TabListElement element : this.elements) {
                removeAll.removePlayer(element.getUuid());
            }

            this.elements.clear();

            VeraPlayer.getPlayers().values().
                    stream().
                    sorted(Comparator.comparing(p -> p.getTabListName().getText())).
                    forEach(p -> {
                        TabListElement e = new TabListElement(p);
                        this.elements.add(e);
                        addAll.addPlayer(e);
                    });
        }

        for (VeraPlayer p : this.users) {
            p.net().sendPacket(removeAll);
            p.net().sendPacket(addAll);
        }
    }

    @Override
    public void unsubscribe(Player player) {
        super.unsubscribe(player);
    }
}