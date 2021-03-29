package io.vera.server.inventory;

import io.vera.inventory.Inventory;
import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.inventory.Substance;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutOpenWindow;
import io.vera.server.packet.play.PlayOutSlot;
import io.vera.server.packet.play.PlayOutWindowItems;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import io.vera.util.Tuple;
import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraInventory implements Inventory {
  @ConstructorProperties({"type", "size"})
  public VeraInventory(InventoryType type, int size) {
    this.id = calculateNextId();
    this.title = ChatComponent.text("Inventory");
    this.contents = new ConcurrentHashMap<>();
    this.type = type;
    this.size = size;
  }
  
  private static final Map<Integer, Tuple<VeraInventory, Set<VeraPlayer>>> REGISTERED_WINDOWS = new ConcurrentHashMap<>();
  
  private final int id;
  
  private final InventoryType type;
  
  private final int size;
  
  private volatile ChatComponent title;
  
  protected final Map<Integer, Item> contents;
  
  public int getId() {
    return this.id;
  }
  
  public InventoryType getType() {
    return this.type;
  }
  
  public int getSize() {
    return this.size;
  }
  
  public ChatComponent getTitle() {
    return this.title;
  }
  
  public Map<Integer, Item> getContents() {
    return this.contents;
  }
  
  public static void open(VeraInventory inventory, VeraPlayer player) {
    REGISTERED_WINDOWS.compute(Integer.valueOf(inventory.getId()), (k, v) -> {
          if (v == null) {
            Set<VeraPlayer> players = Collections.newSetFromMap(new WeakHashMap<>());
            players.add(player);
            return new Tuple(inventory, players);
          } 
          ((Set<VeraPlayer>)v.getB()).add(player);
          return v;
        });
    player.net().sendPacket((PacketOut)new PlayOutOpenWindow(inventory, null));
    player.net().sendPacket((PacketOut)new PlayOutWindowItems(inventory));
  }
  
  public static void close(int id, VeraPlayer player) {
    REGISTERED_WINDOWS.computeIfPresent(Integer.valueOf(id), (k, v) -> {
          Set<VeraPlayer> b = (Set<VeraPlayer>)v.getB();
          b.remove(player);
          return b.isEmpty() ? null : v;
        });
  }
  
  public static void clean() {
    for (Integer key : REGISTERED_WINDOWS.keySet())
      REGISTERED_WINDOWS.computeIfPresent(key, (k, v) -> ((Set)v.getB()).isEmpty() ? null : v); 
  }
  
  private static int calculateNextId() {
    int t;
    do {
      t = ThreadLocalRandom.current().nextInt(255) + 1;
    } while (REGISTERED_WINDOWS.containsKey(Integer.valueOf(t)));
    return t;
  }
  
  public boolean add(Item item, int quantity) {
    if (quantity < 0)
      throw new IllegalArgumentException("Quantity cannot be negative"); 
    Substance s = item.getSubstance();
    if (s == Substance.AIR)
      throw new IllegalArgumentException("Cannot put AIR into inventory"); 
    int maxStack = maxStack(s);
    for (Map.Entry<Integer, Item> entry : this.contents.entrySet()) {
      if (((Item)entry.getValue()).getSubstance() == s) {
        int available = maxStack - ((Item)entry.getValue()).getCount();
        if (quantity > available) {
          quantity -= available;
          VeraItem veraItem = new VeraItem(s, maxStack, item.getDamage(), item.getMeta());
          sendViewers((PacketOut)new PlayOutSlot(this.id, ((Integer)entry.getKey()).intValue(), Slot.newSlot(veraItem)));
          entry.setValue(veraItem);
          continue;
        } 
        VeraItem value = new VeraItem(s, quantity, item.getDamage(), item.getMeta());
        sendViewers((PacketOut)new PlayOutSlot(this.id, ((Integer)entry.getKey()).intValue(), Slot.newSlot(value)));
        entry.setValue(value);
        return true;
      } 
    } 
    Item put = new VeraItem(s, Math.min(quantity, maxStack), item.getDamage(), item.getMeta());
    int slot = -1;
    for (Map.Entry<Integer, Item> entry : this.contents.entrySet()) {
      slot++;
      int key = ((Integer)entry.getKey()).intValue();
      if (key > slot)
        for (int i = slot; i < key; i++) {
          Item result = this.contents.putIfAbsent(Integer.valueOf(i), put);
          if (result == null) {
            sendViewers((PacketOut)new PlayOutSlot(this.id, i, Slot.newSlot(put)));
            quantity -= put.getCount();
            if (quantity > 0) {
              put = new VeraItem(s, Math.min(quantity, maxStack), item.getDamage(), item.getMeta());
            } else {
              return true;
            } 
          } 
        }  
    } 
    return false;
  }
  
  public Item add(int slot, Item item, int quantity) {
    if (quantity < 0)
      throw new IllegalArgumentException("Quantity cannot be negative"); 
    if (slot < 0 || slot >= this.size)
      throw new IllegalArgumentException("Illegal slot " + slot); 
    Substance sub = item.getSubstance();
    if (sub == Substance.AIR)
      throw new IllegalArgumentException("Cannot put AIR into inventory"); 
    Item computed = this.contents.compute(Integer.valueOf(slot), (k, v) -> {
          if (v == null) {
            VeraItem tItem = new VeraItem(sub, Math.min(maxStack(sub), item.getCount()), item.getDamage(), item.getMeta());
            sendViewers((PacketOut)new PlayOutSlot(this.id, slot, Slot.newSlot(tItem)));
            return tItem;
          } 
          if (v.getSubstance() == sub) {
            VeraItem tItem = new VeraItem(sub, Math.min(maxStack(sub), v.getCount() + item.getCount()), item.getDamage(), item.getMeta());
            sendViewers((PacketOut)new PlayOutSlot(this.id, slot, Slot.newSlot(tItem)));
            return tItem;
          } 
          return v;
        });
    return (computed.getSubstance() == sub) ? null : computed;
  }
  
  @Nonnull
  public Item remove(int slot, int quantity) {
    if (quantity < 0)
      throw new IllegalArgumentException("Quantity cannot be negative"); 
    if (slot < 0 || slot >= this.size)
      throw new IllegalArgumentException("Illegal slot " + slot); 
    Item computed = this.contents.compute(Integer.valueOf(slot), (k, v) -> {
          if (v == null)
            return null; 
          int left = Math.max(0, v.getCount() - quantity);
          if (left == 0) {
            sendViewers((PacketOut)new PlayOutSlot(this.id, slot, Slot.EMPTY));
            return null;
          } 
          VeraItem item = new VeraItem(v.getSubstance(), left, v.getDamage(), v.getMeta());
          PlayOutSlot playOutSlot = new PlayOutSlot(this.id, slot, Slot.newSlot(item));
          sendViewers((PacketOut)playOutSlot);
          return item;
        });
    return (computed == null) ? VeraItem.EMPTY : computed;
  }
  
  @Nonnull
  public Item get(int slot) {
    if (slot < 0 || slot >= this.size)
      throw new IllegalArgumentException("Illegal slot " + slot); 
    Item item = this.contents.get(Integer.valueOf(slot));
    return (item == null) ? VeraItem.EMPTY : item;
  }
  
  public void setTitle(ChatComponent title) {
    this.title = title;
  }
  
  protected void sendViewers(PacketOut packetOut) {
    Tuple<VeraInventory, Set<VeraPlayer>> tuple = REGISTERED_WINDOWS.get(Integer.valueOf(this.id));
    if (tuple != null) {
      Set<VeraPlayer> players = (Set<VeraPlayer>)tuple.getB();
      for (VeraPlayer player : players)
        player.net().sendPacket(packetOut); 
    } 
  }
  
  protected static int maxStack(Substance s) {
    if (s.hasDurability() || s == Substance.BOOK_AND_QUILL || s == Substance.ENCHANTED_BOOK || s == Substance.LAVA_BUCKET)
      return 1; 
    if (s == Substance.SNOWBALL || s == Substance.BUCKET || s == Substance.EGG || s == Substance.ENDER_PEARL || s == Substance.SIGN)
      return 16; 
    return 64;
  }
}
