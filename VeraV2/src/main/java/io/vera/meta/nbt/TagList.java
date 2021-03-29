package io.vera.meta.nbt;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class TagList<E> extends ArrayList<E> {
  private final Tag.Type type;
  
  @ConstructorProperties({"type"})
  public TagList(Tag.Type type) {
    this.type = type;
  }
  
  public Tag.Type getType() {
    return this.type;
  }
}
