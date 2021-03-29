package io.vera.server.ui.tablist;

import java.beans.ConstructorProperties;
import lombok.NonNull;

public class PlayerProperty {
  private final String name;
  
  @NonNull
  private final String value;
  
  private final String signature;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerProperty))
      return false; 
    PlayerProperty other = (PlayerProperty)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$value = getValue(), other$value = other.getValue();
    if ((this$value == null) ? (other$value != null) : !this$value.equals(other$value))
      return false; 
    Object this$signature = getSignature(), other$signature = other.getSignature();
    return !((this$signature == null) ? (other$signature != null) : !this$signature.equals(other$signature));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerProperty;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $value = getValue();
    result = result * 59 + (($value == null) ? 43 : $value.hashCode());
    Object $signature = getSignature();
    return result * 59 + (($signature == null) ? 43 : $signature.hashCode());
  }
  
  public String toString() {
    return "PlayerProperty(name=" + getName() + ", value=" + getValue() + ", signature=" + getSignature() + ")";
  }
  
  @ConstructorProperties({"name", "value", "signature"})
  public PlayerProperty(String name, @NonNull String value, String signature) {
    if (value == null)
      throw new NullPointerException("value"); 
    this.name = name;
    this.value = value;
    this.signature = signature;
  }
  
  public String getName() {
    return this.name;
  }
  
  @NonNull
  public String getValue() {
    return this.value;
  }
  
  public String getSignature() {
    return this.signature;
  }
}
