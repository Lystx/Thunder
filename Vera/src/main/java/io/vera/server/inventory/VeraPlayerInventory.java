
package io.vera.server.inventory;

import io.vera.server.net.NetClient;
import io.vera.server.net.Slot;
import lombok.Getter;
import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.inventory.PlayerInventory;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutEquipment;
import io.vera.server.packet.play.PlayOutSlot;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class VeraPlayerInventory extends VeraInventory implements PlayerInventory {

    @Getter
    private volatile int selectedSlot;
    private final NetClient client;

    public VeraPlayerInventory(NetClient client) {
        super(InventoryType.PLAYER, 46);
        this.client = client;
    }

    @Override
    public boolean add(Item item, int quantity) {
        boolean add = super.add(item, quantity);
        this.update();
        return add;
    }

    @Override
    public Item add(int slot, Item item, int quantity) {
        Item add = super.add(slot, item, quantity);

        VeraPlayer player = this.client.getPlayer();
        if (slot == 5) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 5, this.get(5)));
        }
        if (slot == 6) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 4, this.get(6)));
        }
        if (slot == 7) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 3, this.get(7)));
        }
        if (slot == 8) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 2, this.get(8)));
        }
        if (slot == 45) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 1, this.get(45)));
        }
        int heldSlot = this.selectedSlot;
        do {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.get(36 + heldSlot)));
        } while (heldSlot != this.selectedSlot);

        return add;
    }

    @Nonnull
    @Override
    public Item getHeldItem() {
        return this.get(36 + this.selectedSlot);
    }

    @Nonnull
    @Override
    public Item getOffHeldItem() {
        return this.get(45);
    }

    @Override
    protected void sendViewers(PacketOut packetOut) {
        this.client.sendPacket(packetOut);
    }

    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;

        VeraPlayer player = this.client.getPlayer();
        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.getHeldItem()));
    }

    public void update() {
        VeraPlayer player = this.client.getPlayer();
        this.contents.forEach((integer, item) -> {
            int i = integer.intValue();
            if (i == 5) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 5, item));
            }
            if (i == 6) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 4, item));
            }
            if (i == 7) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 3, item));
            }
            if (i == 8) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 2, item));
            }

            this.client.sendPacket(new PlayOutSlot(0, i, Slot.newSlot(item)));
        });

        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 1, this.getOffHeldItem()));
        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.getHeldItem()));
    }
}