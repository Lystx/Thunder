
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.event.player.PlayerInteractEvent;
import io.vera.inventory.Item;
import io.vera.inventory.PlayerInventory;
import io.vera.server.VeraServer;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayInUseItem extends PacketIn {

    public PlayInUseItem() {
        super(PlayInUseItem.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        VeraPlayer player = client.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Item cur = NetData.rvint(buf) == 0 ? inventory.getHeldItem() : inventory.getOffHeldItem();
        VeraServer.getInstance().getEventController().callEvent(new PlayerInteractEvent(player), e -> {
            if (!e.isCancelled()) {
                // TODO actions
            }
        });
    }
}