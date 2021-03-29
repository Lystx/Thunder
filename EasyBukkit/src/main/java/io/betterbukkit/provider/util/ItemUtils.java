package io.betterbukkit.provider.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.betterbukkit.provider.item.Item;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils extends Util<Item, ItemStack> {

    @Override
    public Item from(ItemStack itemStack) {
        return null;
    }

    @Override
    public ItemStack to(Item itemUtilsItemImpl) {
        if (itemUtilsItemImpl == null) {
            return null;
        }
        InternalItem itemStack = new InternalItem(
                Material.valueOf(itemUtilsItemImpl.getMaterial()),
                itemUtilsItemImpl.getId(),
                itemUtilsItemImpl.getAmount()
        );
        itemStack.addLoreAll(itemUtilsItemImpl.getLore());
        itemStack.setUnbreakable(itemUtilsItemImpl.isUnbreakable());
        if (itemUtilsItemImpl.isGlow()) {
            itemStack.setGlow();
        }
        itemStack.setDisplayName(itemUtilsItemImpl.getDisplayName());
        if (itemUtilsItemImpl.getSkullOwner() != null) {
            itemStack.setSkullOwner(itemUtilsItemImpl.getSkullOwner());
        }
        return itemStack.build();
    }

    private class InternalItem {

        private ItemStack item;
        private List<String> lore = new ArrayList<String>();
        private ItemMeta meta;
        private short subid;

        public InternalItem(Material mat, short subid, int amount) {
            this.subid = subid;
            this.item = new ItemStack(mat, amount, subid);
            meta = item.getItemMeta();
        }


        public InternalItem(ItemStack item) {
            this.item = item;
            this.meta = item.getItemMeta();
        }
        public InternalItem(Material mat, short subid) {
            this.subid = subid;
            this.item  = new ItemStack(mat, 1, subid);
            meta = item.getItemMeta();
        }

        public InternalItem(Material mat, int amount) {
            this.item  = new ItemStack(mat, amount, (short)0);
            meta = item.getItemMeta();
        }

        public InternalItem(Material mat) {
            this.item  = new ItemStack(mat, 1, (short)0);
            meta = item.getItemMeta();
        }

        public InternalItem setAmount(int value) {
            item.setAmount(value);
            return this;
        }

        public InternalItem setNoName() {
            meta.setDisplayName(" ");
            return this;
        }
        public InternalItem setGlow() {
            meta.addEnchant( Enchantment.DURABILITY, 1, true);
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS);
            return this;
        }

        public InternalItem setData(short data) {
            item.setDurability(data);
            return this;
        }

        public InternalItem addLoreLine(String line) {
            lore.add(line);
            return this;
        }

        public InternalItem addLoreArray(List<String> lines) {
            lore.addAll(lines);
            return this;
        }

        public InternalItem addLoreAll(List<String> lines) {
            lore.addAll(lines);
            return this;
        }

        public InternalItem setDisplayName(String name) {
            meta.setDisplayName(name);
            return this;
        }

        public InternalItem setSkullOwner(String owner) {
            ((SkullMeta)meta).setOwner(owner);
            return this;
        }

        public InternalItem setColor(Color c) {
            ((LeatherArmorMeta)meta).setColor(c);
            return this;
        }

        public InternalItem setBannerColor(DyeColor c) {
            ((BannerMeta)meta).setBaseColor(c);
            return this;
        }

        public InternalItem setUnbreakable(boolean value) {
            meta.spigot().setUnbreakable(value);
            return this;
        }

        public InternalItem addEnchantment(Enchantment ench, int lvl) {
            meta.addEnchant(ench, lvl, true);
            return this;
        }

        public InternalItem addItemFlag(ItemFlag flag) {
            meta.addItemFlags(flag);
            return this;
        }


        public InternalItem addLeatherColor(Color color) {
            ((LeatherArmorMeta ) meta).setColor( color );
            return this;
        }

        public InternalItem setSkullTextures(String value, String signature) {
            try {
                SkullMeta skullMeta = (SkullMeta) meta;
                final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) null);
                gameProfile.getProperties().put("textures", new Property("textures", value, signature));
                final Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
                item.setItemMeta(skullMeta);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }


        public ItemStack build() {
            if(!lore.isEmpty()) {
                meta.setLore(lore);
            }
            String name = meta.getDisplayName();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            return item;
        }


    }
}
