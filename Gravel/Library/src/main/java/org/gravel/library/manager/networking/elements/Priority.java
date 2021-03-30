package org.gravel.library.manager.networking.elements;

public enum Priority {
  HIGH(-1),
  NORMAL(0),
  LOW(1);
  
  Priority(int value) {
    this.value = value;
  }
  
  public final int value;
  
  public int getValue() {
    return this.value;
  }
}
