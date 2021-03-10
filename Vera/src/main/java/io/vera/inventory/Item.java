
package io.vera.inventory;

import io.vera.meta.ItemMeta;
import io.vera.Impl;

import javax.annotation.concurrent.Immutable;


@Immutable
public interface Item {

    static Item newItem(Substance substance) {
        return newItem(substance, 1);
    }

    static Item newItem(Substance substance, int count) {
        return newItem(substance, count, (byte) 0);
    }

    static Item newItem(Substance substance, int count, byte damage) {
        return newItem(substance, count, damage, new ItemMeta());
    }

    static Item newItem(Substance substance, int count, byte damage, ItemMeta meta) {
        return Impl.get().newItem(substance, count, damage, meta);
    }

    Substance getSubstance();

    int getCount();

    byte getDamage();

    ItemMeta getMeta();

    boolean isEmpty();
}