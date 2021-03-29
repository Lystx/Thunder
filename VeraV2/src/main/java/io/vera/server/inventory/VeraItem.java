package io.vera.server.inventory;

import io.vera.inventory.Item;
import io.vera.inventory.Substance;
import io.vera.meta.ItemMeta;
import java.beans.ConstructorProperties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class VeraItem implements Item {
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof VeraItem))
      return false; 
    VeraItem other = (VeraItem)o;
    if (!other.canEqual(this))
      return false; 
    Object this$substance = getSubstance(), other$substance = other.getSubstance();
    if ((this$substance == null) ? (other$substance != null) : !this$substance.equals(other$substance))
      return false; 
    if (getCount() != other.getCount())
      return false; 
    if (getDamage() != other.getDamage())
      return false; 
    Object this$meta = getMeta(), other$meta = other.getMeta();
    return !((this$meta == null) ? (other$meta != null) : !this$meta.equals(other$meta));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof VeraItem;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $substance = getSubstance();
    result = result * 59 + (($substance == null) ? 43 : $substance.hashCode());
    result = result * 59 + getCount();
    result = result * 59 + getDamage();
    Object $meta = getMeta();
    return result * 59 + (($meta == null) ? 43 : $meta.hashCode());
  }
  
  @ConstructorProperties({"substance", "count", "damage", "meta"})
  public VeraItem(Substance substance, int count, byte damage, ItemMeta meta) {
    this.substance = substance;
    this.count = count;
    this.damage = damage;
    this.meta = meta;
  }
  
  public static final VeraItem EMPTY = new VeraItem(Substance.AIR, 0, (byte)0, new ItemMeta());
  
  private final Substance substance;
  
  private final int count;
  
  private final byte damage;
  
  private final ItemMeta meta;
  
  public Substance getSubstance() {
    return this.substance;
  }
  
  public int getCount() {
    return this.count;
  }
  
  public byte getDamage() {
    return this.damage;
  }
  
  public ItemMeta getMeta() {
    return this.meta;
  }
  
  public boolean isEmpty() {
    return (this.substance == Substance.AIR);
  }
}
