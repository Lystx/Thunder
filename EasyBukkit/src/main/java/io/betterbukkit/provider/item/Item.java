package io.betterbukkit.provider.item;

import io.betterbukkit.provider.util.ItemUtils;
import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
public class Item implements Serializable {

    private final List<String> lore;
    private final short id;
    private final String material;

    private String displayName;
    private int amount;
    private boolean unbreakable;
    private boolean glow;

    private String skullOwner;

    public Item(Substance material, short subid, int amount) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    public Item(String material, short subid, int amount) {
        this(new LinkedList<>(), material, amount, subid);
    }

    public Item(Substance material, int amount, short subid) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    public Item(String material, int amount, short subid) {
        this(new LinkedList<>(), material, amount, subid);
    }

    public Item(Substance material, short subid) {
        this(new LinkedList<>(), material.name(), 1, subid);
    }

    public Item(String material, short subid) {
        this(new LinkedList<>(), material, 1, subid);
    }

    public Item(Substance material, int amount) {
        this(new LinkedList<>(), material.name(), amount, (short) 0);
    }

    public Item(String material, int amount) {
        this(new LinkedList<>(), material, amount, (short) 0);
    }

    public Item(Substance material) {
        this(new LinkedList<>(), material.name(), 1, (short) 0);
    }
    public Item(String material) {
        this(new LinkedList<>(), material, 1, (short) 0);
    }

    public Item(List<String> lore, String material, int amount, short id) {
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.id = id;
        this.displayName = "";
    }

    public Item setAmount(int value) {
        this.amount = value;
        return this;
    }

    public Item noName() {
        this.displayName = "§8";
        return this;
    }

    public Item glow() {
        this.glow = true;
        return this;
    }

    public Item setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public Item addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public Item addLoreArray(List<String> lines) {
        this.lore.addAll(lines);
        return this;
    }

    public Item setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public Item unbreakable() {
        this.unbreakable = true;
        return this;
    }

}
