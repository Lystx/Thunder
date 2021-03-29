package io.vera.server.inventory;

import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.inventory.PlayerInventory;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetClient;
import io.vera.server.net.Slot;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutEquipment;
import io.vera.server.packet.play.PlayOutSlot;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraPlayerInventory extends VeraInventory implements PlayerInventory {
  private volatile int selectedSlot;
  
  private final NetClient client;
  
  public int getSelectedSlot() {
    return this.selectedSlot;
  }
  
  public VeraPlayerInventory(NetClient client) {
    super(InventoryType.PLAYER, 46);
    this.client = client;
  }
  
  public boolean add(Item item, int quantity) {
    boolean add = super.add(item, quantity);
    update();
    return add;
  }
  
  public Item add(int slot, Item item, int quantity) {
    Item add = super.add(slot, item, quantity);
    VeraPlayer player = this.client.getPlayer();
    if (slot == 5)
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 5, get(5)) }); 
    if (slot == 6)
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 4, get(6)) }); 
    if (slot == 7)
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 3, get(7)) }); 
    if (slot == 8)
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 2, get(8)) }); 
    if (slot == 45)
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 1, get(45)) }); 
    int heldSlot = this.selectedSlot;
    do {
      RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 0, get(36 + heldSlot)) });
    } while (heldSlot != this.selectedSlot);
    return add;
  }
  
  @Nonnull
  public Item getHeldItem() {
    return get(36 + this.selectedSlot);
  }
  
  @Nonnull
  public Item getOffHeldItem() {
    return get(45);
  }
  
  protected void sendViewers(PacketOut packetOut) {
    this.client.sendPacket(packetOut);
  }
  
  public void setSelectedSlot(int slot) {
    this.selectedSlot = slot;
    VeraPlayer player = this.client.getPlayer();
    RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 0, getHeldItem()) });
  }
  
  public void update() {
    VeraPlayer player = this.client.getPlayer();
    this.contents.forEach((integer, item) -> {
          int i = integer.intValue();
          if (i == 5)
            RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 5, item) }); 
          if (i == 6)
            RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 4, item) }); 
          if (i == 7)
            RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 3, item) }); 
          if (i == 8)
            RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 2, item) }); 
          this.client.sendPacket((PacketOut)new PlayOutSlot(0, i, Slot.newSlot(item)));
        });
    RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 1, getOffHeldItem()) });
    RecipientSelector.whoCanSee((VeraEntity)player, true, new PacketOut[] { (PacketOut)new PlayOutEquipment((VeraEntity)player, 0, getHeldItem()) });
  }
}
