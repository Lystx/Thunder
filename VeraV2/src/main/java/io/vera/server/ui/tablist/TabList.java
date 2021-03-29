package io.vera.server.ui.tablist;

import io.vera.entity.living.Player;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@Getter
public abstract class TabList {

    protected final Collection<VeraPlayer> users = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final List<TabListElement> elements = new ArrayList<>();
    protected final Object lock = new Object();
    private volatile ChatComponent header;
    private volatile ChatComponent footer;

    public void setHeader(ChatComponent value) {
        this.header = value;
        updateHeaderFooter();
    }

    public void setFooter(ChatComponent value) {
        this.footer = value;
        updateHeaderFooter();
    }

    public Collection<Player> getUserList() {
        return (Collection)Collections.unmodifiableCollection(this.users);
    }

    public void subscribe(Player player) {
        VeraPlayer tridentPlayer = (VeraPlayer)player;
        this.users.add(tridentPlayer);
        PlayOutTabListItem.AddPlayer addPacket = PlayOutTabListItem.addPlayerPacket();
        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        TabListElement element = new TabListElement(tridentPlayer);
        synchronized (this.lock) {
            this.elements.add(element);
            this.elements.forEach(addPacket::addPlayer);
        }
        NetClient net = tridentPlayer.net();
        net.sendPacket((PacketOut)addPacket);
        net.sendPacket((PacketOut)headerAndFooterPacket);
        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(element);
        for (VeraPlayer tp : this.users)
            tp.net().sendPacket((PacketOut)addMe);
    }

    public void unsubscribe(Player player) {
        VeraPlayer tridentPlayer = (VeraPlayer)player;
        this.users.remove(tridentPlayer);
        PlayOutTabListItem.RemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
        synchronized (this.lock) {
            for (Iterator<TabListElement> it = this.elements.iterator(); it.hasNext(); ) {
                TabListElement element = it.next();
                if (element.getUuid().equals(tridentPlayer.getUuid()))
                    it.remove();
                packet.removePlayer(element.getUuid());
            }
        }
        NetClient net = tridentPlayer.net();
        net.sendPacket((PacketOut)packet);
        net.sendPacket((PacketOut)new PlayOutPlayerListHeaderAndFooter(ChatComponent.empty(), ChatComponent.empty()));
        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(tridentPlayer.getUuid());
        for (VeraPlayer tp : this.users)
            tp.net().sendPacket((PacketOut)removeMe);
    }

    public void updateTabListName(Player player) {
        PlayOutTabListItem.UpdateDisplayName updateDisplayName = PlayOutTabListItem.updatePlayerPacket();
        updateDisplayName.update(player.getUuid(), player.getTabListName());
        for (VeraPlayer tp : this.users)
            tp.net().sendPacket((PacketOut)updateDisplayName);
    }

    public void update(VeraPlayer player) {
        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(player.getUuid());
        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(new TabListElement(player));
        for (VeraPlayer tp : this.users) {
            tp.net().sendPacket((PacketOut)removeMe);
            tp.net().sendPacket((PacketOut)addMe);
        }
    }

    private void updateHeaderFooter() {
        PlayOutPlayerListHeaderAndFooter packet = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        this.users.forEach(player -> player.net().sendPacket((PacketOut)packet));
    }
}
