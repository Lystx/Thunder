
package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.vera.meta.ItemMeta;
import io.vera.meta.nbt.Compound;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.vera.inventory.Substance;
import io.vera.inventory.Item;
import io.vera.meta.nbt.Tag;
import io.vera.server.inventory.VeraItem;

import javax.annotation.concurrent.Immutable;
import java.io.DataInputStream;
import java.io.DataOutputStream;

@Immutable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Slot {

    public static final Slot EMPTY = new Slot((short) -1, (byte) 0, (short) 0, new ItemMeta());

    @Getter
    private final short id;
    @Getter
    private final byte count;
    @Getter
    private final short damage;
    @Getter
    private final ItemMeta meta;

    public static Slot read(ByteBuf buf) {
        short id = buf.readShort();
        if (id != -1) {
            byte count = buf.readByte();
            short dmg = buf.readShort();
            Compound nbt = Tag.decode(new DataInputStream(new ByteBufInputStream(buf)));

            if (id == Substance.AIR.getId()) {
                return EMPTY;
            } else {
                return new Slot(id, count, dmg, nbt == null ? new ItemMeta() : new ItemMeta(nbt));
            }
        } else {
            return EMPTY;
        }
    }

    public static Slot newSlot(Item item) {
        if (item.isEmpty()) {
            return EMPTY;
        }

        return new Slot((short) item.getSubstance().getId(),
                (byte) item.getCount(),
                item.getDamage(), item.getMeta());
    }


    public VeraItem toItem() {
        return new VeraItem(Substance.fromNumericId(this.id), this.count, (byte) this.damage, this.meta);
    }

    public void write(ByteBuf buf) {
        buf.writeShort(this.id);

        if (this.id != -1) {
            buf.writeByte(this.count);
            buf.writeShort(this.damage);
            this.meta.writeNbt(new DataOutputStream(new ByteBufOutputStream(buf)));
        }
    }
}