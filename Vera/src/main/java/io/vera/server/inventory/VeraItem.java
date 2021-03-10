
package io.vera.server.inventory;

import io.vera.inventory.Substance;
import io.vera.meta.ItemMeta;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import io.vera.inventory.Item;

import javax.annotation.concurrent.Immutable;

@Immutable
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class VeraItem implements Item {

    public static final VeraItem EMPTY = new VeraItem(Substance.AIR, 0, (byte) 0, new ItemMeta());

    private final Substance substance;
    private final int count;
    private final byte damage;
    private final ItemMeta meta;

    @Override
    public boolean isEmpty() {
        return this.substance == Substance.AIR;
    }
}