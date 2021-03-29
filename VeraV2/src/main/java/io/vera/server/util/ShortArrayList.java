package io.vera.server.util;

import java.util.Arrays;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ShortArrayList {
  public static final int MAX_ARRAY_SIZE = 2147483639;
  
  public static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  protected transient short[] a;
  
  protected int size;
  
  private static final boolean ASSERTS = false;
  
  public static short[] grow(short[] array, int length, int preserve) {
    if (length > array.length) {
      int newLength = (int)Math.max(
          Math.min(2L * array.length, 2147483639L), length);
      short[] t = new short[newLength];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
    } 
    return array;
  }
  
  public static short[] ensureCapacity(short[] array, int length, int preserve) {
    if (length > array.length) {
      short[] t = new short[length];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
    } 
    return array;
  }
  
  protected void ensureIndex(int index) {
    if (index < 0)
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
    if (index > size())
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + 
          size() + ")"); 
  }
  
  protected ShortArrayList(short[] a, boolean dummy) {
    this.a = a;
  }
  
  public ShortArrayList(int capacity) {
    if (capacity < 0)
      throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative"); 
    this.a = new short[capacity];
  }
  
  public ShortArrayList() {
    this(16);
  }
  
  public void ensureCapacity(int capacity) {
    this.a = ensureCapacity(this.a, capacity, this.size);
  }
  
  private void grow(int capacity) {
    this.a = grow(this.a, capacity, this.size);
  }
  
  public void add(int index, short k) {
    ensureIndex(index);
    grow(this.size + 1);
    if (index != this.size)
      System.arraycopy(this.a, index, this.a, index + 1, this.size - index); 
    this.a[index] = k;
    this.size++;
  }
  
  public int add(short k) {
    grow(this.size + 1);
    int idx = this.size++;
    this.a[idx] = k;
    return idx;
  }
  
  public short getShort(int index) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    return this.a[index];
  }
  
  public int indexOf(short k) {
    for (int i = 0; i < this.size; i++) {
      if (k == this.a[i])
        return i; 
    } 
    return -1;
  }
  
  public short set(int index, short k) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    short old = this.a[index];
    this.a[index] = k;
    return old;
  }
  
  public int size() {
    return this.size;
  }
  
  public void size(int size) {
    if (size > this.a.length)
      ensureCapacity(size); 
    if (size > this.size)
      Arrays.fill(this.a, this.size, size, (short)0); 
    this.size = size;
  }
}
