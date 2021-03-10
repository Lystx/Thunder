
package io.vera.server.ui.tablist;

import io.vera.server.net.NetClient;
import lombok.Getter;
import io.vera.entity.living.Player;
import io.vera.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@ThreadSafe
public abstract class TabList {
    
    protected final Collection<VeraPlayer> users = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    @GuardedBy("lock")
    protected final List<TabListElement> elements = new ArrayList<>();
    protected final Object lock = new Object();
    @Getter
    private volatile ChatComponent header;
    @Getter
    private volatile ChatComponent footer;

    
    public void setHeader(ChatComponent value) {
        this.header = value;
        this.updateHeaderFooter();
    }

    
    public void setFooter(ChatComponent value) {
        this.footer = value;
        this.updateHeaderFooter();
    }

    
    public Collection<Player> getUserList() {
        return Collections.unmodifiableCollection(this.users);
    }

    public void subscribe(Player player) {
        VeraPlayer tridentPlayer = (VeraPlayer) player;
        this.users.add(tridentPlayer);

        PlayOutTabListItem.AddPlayer addPacket = PlayOutTabListItem.addPlayerPacket();
        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        TabListElement element = new TabListElement(tridentPlayer);

        synchronized (this.lock) {
            this.elements.add(element);
            this.elements.forEach(addPacket::addPlayer);
        }

        NetClient net = tridentPlayer.net();
        net.sendPacket(addPacket);
        net.sendPacket(headerAndFooterPacket);

        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(element);
        for (VeraPlayer tp : this.users) {
            tp.net().sendPacket(addMe);
        }
    }

    public void unsubscribe(Player player) {
        VeraPlayer tridentPlayer = (VeraPlayer) player;
        this.users.remove(tridentPlayer);

        PlayOutTabListItem.RemovePlayer packet = PlayOutTabListItem.removePlayerPacket();

        synchronized (this.lock) {
            for (Iterator<TabListElement> it = this.elements.iterator(); it.hasNext(); ) {
                TabListElement element = it.next();
                if (element.getUuid().equals(tridentPlayer.getUuid())) {
                    it.remove();
                }

                packet.removePlayer(element.getUuid());
            }
        }

        NetClient net = tridentPlayer.net();
        net.sendPacket(packet);
        net.sendPacket(new PlayOutPlayerListHeaderAndFooter(ChatComponent.empty(), ChatComponent.empty()));

        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(tridentPlayer.getUuid());
        for (VeraPlayer tp : this.users) {
            tp.net().sendPacket(removeMe);
        }
    }

    public void updateTabListName(Player player) {
        PlayOutTabListItem.UpdateDisplayName updateDisplayName = PlayOutTabListItem.updatePlayerPacket();
        updateDisplayName.update(player.getUuid(), player.getTabListName());

        for (VeraPlayer tp : this.users) {
            tp.net().sendPacket(updateDisplayName);
        }
    }

    public void update(VeraPlayer player) {
        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(player.getUuid());

        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(new TabListElement(player));

        for (VeraPlayer tp : this.users) {
            tp.net().sendPacket(removeMe);
            tp.net().sendPacket(addMe);
        }
    }

    private void updateHeaderFooter() {
        PlayOutPlayerListHeaderAndFooter packet = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        this.users.forEach(player -> player.net().sendPacket(packet));
    }
}