
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.inventory.Item;
import io.vera.server.inventory.VeraPlayerInventory;
import io.vera.server.net.NetClient;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;


@Immutable
public class PlayInCreativeInventoryAction extends PacketIn {

    public PlayInCreativeInventoryAction() {
        super(PlayInCreativeInventoryAction.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int slot = buf.readShort();
        Slot item = Slot.read(buf);

        VeraPlayerInventory inventory = client.getPlayer().getInventory();
        if (slot == -1) {
            Item drop = item.toItem();
            return;
        }

        if (item.getId() == -1) {
            inventory.remove(slot, Integer.MAX_VALUE);
        } else {
            inventory.add(slot, item.toItem(), item.getCount());
        }
    }
}