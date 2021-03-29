package io.betterbukkit.provider.util;

import io.betterbukkit.provider.item.Item;
import io.betterbukkit.provider.item.Substance;
import io.betterbukkit.provider.player.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class PlayerInventoryUtils extends Util<PlayerInventory, org.bukkit.inventory.PlayerInventory> {



    @Override
    public PlayerInventory from(org.bukkit.inventory.PlayerInventory itemStacks) {
        return new PlayerInventory() {
            @Override
            public void setArmor(ArmorSlot slot, Item item) {
                if (slot.equals(ArmorSlot.HELMET)) {
                    itemStacks.setHelmet(Util.get(ItemUtils.class).to(item));
                }
                if (slot.equals(ArmorSlot.CHESTPLATE)) {
                    itemStacks.setChestplate(Util.get(ItemUtils.class).to(item));
                }
                if (slot.equals(ArmorSlot.LEGGINGS)) {
                    itemStacks.setLeggings(Util.get(ItemUtils.class).to(item));
                }
                if (slot.equals(ArmorSlot.BOOTS)) {
                    itemStacks.setBoots(Util.get(ItemUtils.class).to(item));
                }
            }

            @Override
            public void setArmor(Substance substance) {
                for (ArmorSlot value : ArmorSlot.values()) {
                    Substance s = Substance.valueOf(substance.name() + "_" + value.name());
                    Item item = new Item(s);
                    setArmor(value, item);
                }
            }

            @Override
            public void clear() {
                itemStacks.clear();
                itemStacks.setArmorContents(null);
            }

            @Override
            public void setItem(int slot, Item item) {
                itemStacks.setItem(slot, Util.get(ItemUtils.class).to(item));
            }

            @Override
            public Item getArmor(ArmorSlot slot) {
                if (slot.equals(ArmorSlot.HELMET)) {
                    return Util.get(ItemUtils.class).from(itemStacks.getHelmet());
                }
                if (slot.equals(ArmorSlot.CHESTPLATE)) {
                    return Util.get(ItemUtils.class).from(itemStacks.getChestplate());
                }
                if (slot.equals(ArmorSlot.LEGGINGS)) {
                    return Util.get(ItemUtils.class).from(itemStacks.getLeggings());
                }
                if (slot.equals(ArmorSlot.BOOTS)) {
                    return Util.get(ItemUtils.class).from(itemStacks.getBoots());
                }
                return null;
            }
        };
    }

    @Override
    public org.bukkit.inventory.PlayerInventory to(PlayerInventory playerInventory) {
        return new org.bukkit.inventory.PlayerInventory() {
            @Override
            public ItemStack[] getArmorContents() {
                return new ItemStack[0];
            }

            @Override
            public ItemStack getHelmet() {
                return Util.get(ItemUtils.class).to(playerInventory.getArmor(PlayerInventory.ArmorSlot.HELMET));
            }

            @Override
            public ItemStack getChestplate() {
                return Util.get(ItemUtils.class).to(playerInventory.getArmor(PlayerInventory.ArmorSlot.CHESTPLATE));
            }

            @Override
            public ItemStack getLeggings() {
                return Util.get(ItemUtils.class).to(playerInventory.getArmor(PlayerInventory.ArmorSlot.LEGGINGS));
            }

            @Override
            public ItemStack getBoots() {
                return Util.get(ItemUtils.class).to(playerInventory.getArmor(PlayerInventory.ArmorSlot.BOOTS));
            }

            @Override
            public void setItem(int i, ItemStack itemStack) {
                playerInventory.setItem(i, Util.get(ItemUtils.class).from(itemStack));
            }

            @Override
            public void setArmorContents(ItemStack[] itemStacks) {

            }

            @Override
            public void setHelmet(ItemStack itemStack) {
                playerInventory.setArmor(PlayerInventory.ArmorSlot.HELMET, Util.get(ItemUtils.class).from(itemStack));
            }

            @Override
            public void setChestplate(ItemStack itemStack) {
                playerInventory.setArmor(PlayerInventory.ArmorSlot.CHESTPLATE, Util.get(ItemUtils.class).from(itemStack));
            }

            @Override
            public void setLeggings(ItemStack itemStack) {
                playerInventory.setArmor(PlayerInventory.ArmorSlot.LEGGINGS, Util.get(ItemUtils.class).from(itemStack));
            }

            @Override
            public void setBoots(ItemStack itemStack) {
                playerInventory.setArmor(PlayerInventory.ArmorSlot.BOOTS, Util.get(ItemUtils.class).from(itemStack));
            }

            @Override
            public ItemStack getItemInHand() {
                return null;
            }

            @Override
            public void setItemInHand(ItemStack itemStack) {

            }

            @Override
            public int getHeldItemSlot() {
                return 0;
            }

            @Override
            public void setHeldItemSlot(int i) {

            }

            @Override
            public int clear(int i, int i1) {
                return 0;
            }

            @Override
            public HumanEntity getHolder() {
                return null;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getMaxStackSize() {
                return 0;
            }

            @Override
            public void setMaxStackSize(int i) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public ItemStack getItem(int i) {
                return null;
            }

            @Override
            public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
                return null;
            }

            @Override
            public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
                return null;
            }

            @Override
            public ItemStack[] getContents() {
                return new ItemStack[0];
            }

            @Override
            public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {

            }

            @Override
            public boolean contains(int i) {
                return false;
            }

            @Override
            public boolean contains(Material material) throws IllegalArgumentException {
                return false;
            }

            @Override
            public boolean contains(ItemStack itemStack) {
                return false;
            }

            @Override
            public boolean contains(int i, int i1) {
                return false;
            }

            @Override
            public boolean contains(Material material, int i) throws IllegalArgumentException {
                return false;
            }

            @Override
            public boolean contains(ItemStack itemStack, int i) {
                return false;
            }

            @Override
            public boolean containsAtLeast(ItemStack itemStack, int i) {
                return false;
            }

            @Override
            public HashMap<Integer, ? extends ItemStack> all(int i) {
                return null;
            }

            @Override
            public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
                return null;
            }

            @Override
            public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
                return null;
            }

            @Override
            public int first(int i) {
                return 0;
            }

            @Override
            public int first(Material material) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public int first(ItemStack itemStack) {
                return 0;
            }

            @Override
            public int firstEmpty() {
                return 0;
            }

            @Override
            public void remove(int i) {

            }

            @Override
            public void remove(Material material) throws IllegalArgumentException {

            }

            @Override
            public void remove(ItemStack itemStack) {

            }

            @Override
            public void clear(int i) {

            }

            @Override
            public void clear() {

            }

            @Override
            public List<HumanEntity> getViewers() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public InventoryType getType() {
                return null;
            }

            @Override
            public ListIterator<ItemStack> iterator() {
                return null;
            }

            @Override
            public ListIterator<ItemStack> iterator(int i) {
                return null;
            }
        };
    }
}
