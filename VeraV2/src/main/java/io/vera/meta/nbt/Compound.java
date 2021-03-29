package io.vera.meta.nbt;

import java.beans.ConstructorProperties;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Compound {
  private final Map<String, Tag> entries;
  
  private final String name;
  
  @ConstructorProperties({"name"})
  public Compound(String name) {
    this.entries = new ConcurrentHashMap<>();
    this.name = name;
  }
  
  public Map<String, Tag> getEntries() {
    return this.entries;
  }
  
  public String getName() {
    return this.name;
  }
  
  @Nullable
  public <T> T get(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      return null; 
    return (T)tag.getObject();
  }
  
  public byte getByte(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Byte)tag.getObject()).byteValue();
  }
  
  public short getShort(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Short)tag.getObject()).shortValue();
  }
  
  public int getInt(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Integer)tag.getObject()).intValue();
  }
  
  public long getLong(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Long)tag.getObject()).longValue();
  }
  
  public float getFloat(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Float)tag.getObject()).floatValue();
  }
  
  public double getDouble(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return ((Double)tag.getObject()).doubleValue();
  }
  
  public byte[] getByteArray(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return (byte[])tag.getObject();
  }
  
  public String getString(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return (String)tag.getObject();
  }
  
  public <T> TagList<T> getList(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return (TagList<T>)tag.getObject();
  }
  
  public Compound getCompound(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return (Compound)tag.getObject();
  }
  
  public int[] getIntArray(String key) {
    Tag tag = this.entries.get(key);
    if (tag == null)
      throw new IllegalArgumentException("No key found for: " + key); 
    return (int[])tag.getObject();
  }
  
  public void putByte(String key, byte i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.BYTE, Byte.valueOf(i)); 
          v.setObject(Byte.valueOf(i));
          return v;
        });
  }
  
  public void putShort(String key, short i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.SHORT, Short.valueOf(i)); 
          v.setObject(Short.valueOf(i));
          return v;
        });
  }
  
  public void putInt(String key, int i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.INT, Integer.valueOf(i)); 
          v.setObject(Integer.valueOf(i));
          return v;
        });
  }
  
  public void putLong(String key, long i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.LONG, Long.valueOf(i)); 
          v.setObject(Long.valueOf(i));
          return v;
        });
  }
  
  public void putFloat(String key, float i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.FLOAT, Float.valueOf(i)); 
          v.setObject(Float.valueOf(i));
          return v;
        });
  }
  
  public void putDouble(String key, double i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.DOUBLE, Double.valueOf(i)); 
          v.setObject(Double.valueOf(i));
          return v;
        });
  }
  
  public void putByteArray(String key, byte[] i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.BYTE_ARRAY, i); 
          v.setObject(i);
          return v;
        });
  }
  
  public void putString(String key, String i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.STRING, i); 
          v.setObject(i);
          return v;
        });
  }
  
  public void putList(String key, TagList<?> i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.LIST, i); 
          v.setObject(i);
          return v;
        });
  }
  
  public void putCompound(Compound i) {
    this.entries.compute(i.getName(), (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.COMPOUND, i); 
          v.setObject(i);
          return v;
        });
  }
  
  public void putIntArray(String key, int[] i) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.INT_ARRAY, i); 
          v.setObject(i);
          return v;
        });
  }
  
  public void computeByte(String key, BiFunction<String, Byte, Byte> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.BYTE, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeShort(String key, BiFunction<String, Short, Short> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.SHORT, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computerInt(String key, BiFunction<String, Integer, Integer> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.INT, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeLong(String key, BiFunction<String, Long, Long> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.LONG, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeFloat(String key, BiFunction<String, Float, Float> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.FLOAT, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeDouble(String key, BiFunction<String, Double, Double> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.DOUBLE, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeByteArray(String key, BiFunction<String, byte[], byte[]> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.BYTE_ARRAY, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeString(String key, BiFunction<String, String, String> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.STRING, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeList(String key, BiFunction<String, TagList<?>, TagList<?>> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.LIST, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeCompound(String key, BiFunction<String, Compound, Compound> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.INT, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public void computeIntArray(String key, BiFunction<String, int[], int[]> func) {
    this.entries.compute(key, (k, v) -> {
          if (v == null)
            return new Tag(Tag.Type.INT_ARRAY, func.apply(k, null)); 
          v.setObject(func.apply(k, v.getObject()));
          return v;
        });
  }
  
  public boolean remove(String key) {
    return (this.entries.remove(key) != null);
  }
  
  public void write(DataOutputStream stream) {
    Tag.Type.COMPOUND.writeFully(this.name, this, stream);
  }
  
  protected void add(Map.Entry<String, Tag> entry) {
    this.entries.put(entry.getKey(), entry.getValue());
  }
}
