package io.vera.server.util;

import io.netty.buffer.ByteBuf;
import java.beans.ConstructorProperties;
import java.util.concurrent.atomic.AtomicLongArray;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;

@ThreadSafe
public class NibbleArray {
  public static final int BYTES_PER_LONG = 8;
  
  @NonNull
  private final AtomicLongArray nibbles;
  
  @ConstructorProperties({"nibbles"})
  public NibbleArray(@NonNull AtomicLongArray nibbles) {
    if (nibbles == null)
      throw new NullPointerException("nibbles"); 
    this.nibbles = nibbles;
  }
  
  public NibbleArray(int length) {
    this.nibbles = new AtomicLongArray(length / 8);
  }
  
  public static byte getNibble(byte[] array, int idx) {
    return (byte)(((idx & 0x1) == 0) ? (array[idx >> 1] & 0xF) : (array[idx >> 1] >> 4 & 0xF));
  }
  
  public static void setNibble(byte[] array, int idx, byte nibble) {
    int i = idx >> 1;
    if ((idx & 0x1) == 0) {
      array[i] = (byte)(array[i] & 0xF0 | nibble & 0xF);
    } else {
      array[i] = (byte)(array[i] & 0xF | nibble << 4 & 0xF0);
    } 
  }
  
  public int getLength() {
    return this.nibbles.length() * 8 << 1;
  }
  
  public byte getByte(int position) {
    int nibblePosition = position / 2;
    long splice = this.nibbles.get(nibblePosition / 8);
    long shift = (nibblePosition % 8 << 3);
    long shifted = splice >> (int)shift;
    if ((position & 0x1) == 0)
      return (byte)(int)(shifted & 0xFL); 
    return (byte)(int)(shifted >> 4L & 0xFL);
  }
  
  public void setByte(int position, byte value) {
    int nibblePosition = position / 2;
    int spliceIndex = nibblePosition >> 3;
    long shift = (nibblePosition % 8 << 3);
    if ((position & 0x1) == 0) {
      long oldSpice;
      long newSplice;
      do {
        oldSpice = this.nibbles.get(spliceIndex);
        long newByte = oldSpice >>> (int)shift & 0xF0L | value;
        newSplice = oldSpice & (255L << (int)shift ^ 0xFFFFFFFFFFFFFFFFL) | newByte << (int)shift;
      } while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
    } else {
      long oldSpice, newSplice, shiftedVal = (value << 4);
      do {
        oldSpice = this.nibbles.get(spliceIndex);
        long newByte = oldSpice >>> (int)shift & 0xFL | shiftedVal;
        newSplice = oldSpice & (255L << (int)shift ^ 0xFFFFFFFFFFFFFFFFL) | newByte << (int)shift;
      } while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
    } 
  }
  
  public void write(ByteBuf buf) {
    for (int i = 0, len = this.nibbles.length(); i < len; i++) {
      long l = this.nibbles.get(i);
      for (int shift = 0; shift < 64; shift += 8) {
        long shifted = l >> shift;
        byte b = (byte)(int)(shifted & 0xFFL);
        buf.writeByte(b);
      } 
    } 
  }
  
  public void fill(byte value) {
    long splice = 0L;
    long newValue = (value << 4 | value & 0xFF);
    int i;
    for (i = 0; i < 64; i += 8)
      splice |= newValue << i; 
    for (i = 0; i < this.nibbles.length(); i++)
      this.nibbles.set(i, splice); 
  }
  
  public void read(byte[] bytes) {
    long cur = 0L;
    for (int i = 0, shift = 0, splice = 0; i < bytes.length; i++) {
      cur |= bytes[i] << shift;
      shift += 8;
      if (shift == 64) {
        this.nibbles.set(splice, cur);
        cur = 0L;
        shift = 0;
        splice++;
      } 
    } 
  }
  
  public byte[] write() {
    byte[] bytes = new byte[this.nibbles.length() * 8];
    for (int i = 0, len = this.nibbles.length(); i < len; i++) {
      long l = this.nibbles.get(i);
      for (int shift = 0, offset = 0; shift < 64; shift += 8, offset++) {
        long shifted = l >> shift;
        byte b = (byte)(int)(shifted & 0xFFL);
        bytes[i * 8 + offset] = b;
      } 
    } 
    return bytes;
  }
}
