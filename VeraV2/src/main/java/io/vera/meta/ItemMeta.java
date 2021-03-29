package io.vera.meta;

import io.vera.meta.nbt.Compound;
import java.io.DataOutputStream;
import javax.annotation.concurrent.Immutable;

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
