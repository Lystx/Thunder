package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.vera.inventory.Item;
import io.vera.inventory.Substance;
import io.vera.meta.ItemMeta;
import io.vera.meta.nbt.Compound;
import io.vera.meta.nbt.Tag;
import io.vera.server.inventory.VeraItem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Slot {
  private Slot(short id, byte count, short damage, ItemMeta meta) {
    this.id = id;
    this.count = count;
    this.damage = damage;
    this.meta = meta;
  }
  
  public static final Slot EMPTY = new Slot((short)-1, (byte)0, (short)0, new ItemMeta());
  
  private final short id;
  
  private final byte count;
  
  private final short damage;
  
  private final ItemMeta meta;
  
  public short getId() {
    return this.id;
  }
  
  public byte getCount() {
    return this.count;
  }
  
  public short getDamage() {
    return this.damage;
  }
  
  public ItemMeta getMeta() {
    return this.meta;
  }
  
  public static Slot read(ByteBuf buf) {
    short id = buf.readShort();
    if (id != -1) {
      byte count = buf.readByte();
      short dmg = buf.readShort();
      Compound nbt = Tag.decode(new DataInputStream((InputStream)new ByteBufInputStream(buf)));
      if (id == Substance.AIR.getId())
        return EMPTY; 
      return new Slot(id, count, dmg, (nbt == null) ? new ItemMeta() : new ItemMeta(nbt));
    } 
    return EMPTY;
  }
  
  public static Slot newSlot(Item item) {
    if (item.isEmpty())
      return EMPTY; 
    return new Slot((short)item.getSubstance().getId(), 
        (byte)item.getCount(), 
        (short)item.getDamage(), item.getMeta());
  }
  
  public VeraItem toItem() {
    return new VeraItem(Substance.fromNumericId(this.id), this.count, (byte)this.damage, this.meta);
  }
  
  public void write(ByteBuf buf) {
    buf.writeShort(this.id);
    if (this.id != -1) {
      buf.writeByte(this.count);
      buf.writeShort(this.damage);
      this.meta.writeNbt(new DataOutputStream((OutputStream)new ByteBufOutputStream(buf)));
    } 
  }
}
