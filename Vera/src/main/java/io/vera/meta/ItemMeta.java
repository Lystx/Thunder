
package io.vera.meta;

import io.vera.meta.nbt.Compound;

import javax.annotation.concurrent.Immutable;
import java.io.DataOutputStream;

@Immutable
public class ItemMeta {

    private final Compound nbt;

    public ItemMeta() {
        this.nbt = new Compound("tag");
    }


    public ItemMeta(Compound compound) {
        this.nbt = compound;
    }

    public void writeNbt(DataOutputStream stream) {
        this.nbt.write(stream);
    }
}