
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.inventory.Item;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;


@Immutable
public class PlayOutEquipment extends PacketOut {

    private final VeraEntity entity;
    private final int slot;
    private final Item item;

    public PlayOutEquipment(VeraEntity entity, int slot, Item item) {
        super(PlayOutEquipment.class);
        this.entity = entity;
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.entity.getId());
        NetData.wvint(buf, this.slot);
        Slot.newSlot(this.item).write(buf);
    }
}