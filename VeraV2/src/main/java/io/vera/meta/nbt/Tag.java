package io.vera.meta.nbt;

import java.beans.ConstructorProperties;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Tag {
  public String toString() {
    return "Tag(type=" + getType() + ", object=" + getObject() + ")";
  }
  
  @ConstructorProperties({"type", "object"})
  public Tag(Type type, Object object) {
    this.type = type;
    this.object = object;
  }
  
  private static final Map.Entry<String, Tag> NULL_ENTRY = new AbstractMap.SimpleEntry<>("", new Tag(Type.END, null));
  
  private final Type type;
  
  private Object object;
  
  public Type getType() {
    return this.type;
  }
  
  public Object getObject() {
    return this.object;
  }
  
  public void setObject(Object object) {
    this.object = object;
  }
  
  public static Compound decode(DataInputStream stream) {
    Map.Entry<String, Tag> root = readFully(stream);
    return (Compound)((Tag)root.getValue()).getObject();
  }
  
  private static Map.Entry<String, Tag> readFully(DataInputStream stream) {
    try {
      int i = stream.readByte();
      Type type = Type.getMapping().get(Integer.valueOf(i));
      if (type == Type.END)
        return NULL_ENTRY; 
      int len = stream.readUnsignedShort();
      if (len > 0) {
        byte[] arr = new byte[len];
        stream.readFully(arr);
        String name = new String(arr, Type.UTF_8);
        Object object = type.read(name, stream);
        return new AbstractMap.SimpleEntry<>(name, new Tag(type, object));
      } 
      Object o = type.read("", stream);
      return new AbstractMap.SimpleEntry<>("", new Tag(type, o));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  @Immutable
  public enum Type {
    END {
      public Object read(String name, DataInputStream stream) {
        return null;
      }
      
      public void write(Object o, DataOutputStream stream) {}
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    BYTE {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Byte.valueOf(stream.readByte());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeByte(((Byte)o).byteValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    SHORT {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Short.valueOf(stream.readShort());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeShort(((Short)o).shortValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    INT {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Integer.valueOf(stream.readInt());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeInt(((Integer)o).intValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    LONG {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Long.valueOf(stream.readLong());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeLong(((Long)o).longValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    FLOAT {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Float.valueOf(stream.readFloat());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeFloat(((Float)o).floatValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    DOUBLE {
      public Object read(String name, DataInputStream stream) throws IOException {
        return Double.valueOf(stream.readDouble());
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        stream.writeDouble(((Double)o).doubleValue());
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    BYTE_ARRAY {
      public Object read(String name, DataInputStream stream) throws IOException {
        int len = stream.readInt();
        byte[] arr = new byte[len];
        stream.readFully(arr);
        return arr;
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        byte[] arr = (byte[])o;
        stream.writeInt(arr.length);
        for (byte b : arr)
          stream.writeByte(b); 
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    STRING {
      public Object read(String name, DataInputStream stream) throws IOException {
        int len = stream.readUnsignedShort();
        byte[] arr = new byte[len];
        stream.readFully(arr);
        return new String(arr, UTF_8);
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        byte[] arr = ((String)o).getBytes(UTF_8);
        stream.writeShort(arr.length);
        for (byte b : arr)
          stream.writeByte(b); 
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    LIST {
      public Object read(String name, DataInputStream stream) throws IOException {
        int type = stream.readByte();
        Type t = (Type)Type.mapping.get(Integer.valueOf(type));
        if (t != null) {
          int len = stream.readInt();
          TagList<?> tagList = t.newListOfType();
          for (int i = 0; i < len; i++)
            tagList.add(t.read("", stream)); 
          return tagList;
        } 
        return Collections.emptyList();
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        TagList tagList = (TagList)o;
        Type type = tagList.getType();
        stream.writeByte(type.ordinal());
        stream.writeInt(tagList.size());
        for (Object obj : tagList)
          type.write(obj, stream); 
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    COMPOUND {
      public Object read(String name, DataInputStream stream) {
        Compound compound = new Compound(name);
        while (true) {
          Map.Entry<String, Tag> tag = Tag.readFully(stream);
          if (((Tag)tag.getValue()).getType() == Type.END)
            break; 
          compound.add(tag);
        } 
        return compound;
      }
      
      public void write(Object o, DataOutputStream stream) {
        Compound compound = (Compound)o;
        for (Map.Entry<String, Tag> entry : compound.getEntries().entrySet())
          ((Tag)entry.getValue()).getType().writeFully(entry.getKey(), ((Tag)entry.getValue()).getObject(), stream); 
        END.writeFully("", null, stream);
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    },
    INT_ARRAY {
      public Object read(String name, DataInputStream stream) throws IOException {
        int len = stream.readInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++)
          arr[i] = stream.readInt(); 
        return arr;
      }
      
      public void write(Object o, DataOutputStream stream) throws IOException {
        int[] arr = (int[])o;
        stream.writeInt(arr.length);
        for (int i : arr)
          stream.writeInt(i); 
      }
      
      public TagList<?> newListOfType() {
        return new TagList(this);
      }
    };
    
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    
    private static final Map<Integer, Type> mapping = new HashMap<Integer, Type>() {
        public Type get(Object key) {
          Type type = super.get(key);
          if (type == null)
            return Type.END;
          return type;
        }
      };
    
    public static Charset getUTF_8() {
      return UTF_8;
    }
    
    static {
      for (Type t : values())
        mapping.put(Integer.valueOf(t.ordinal()), t); 
    }
    
    public static Map<Integer, Type> getMapping() {
      return mapping;
    }
    
    public void writeFully(String name, Object o, DataOutputStream stream) {
      try {
        stream.writeByte(ordinal());
        if (this == END)
          return; 
        byte[] arr = name.getBytes(UTF_8);
        stream.writeShort(arr.length);
        for (byte b : arr)
          stream.writeByte(b); 
        write(o, stream);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
    }
    
    public abstract Object read(String param1String, DataInputStream param1DataInputStream) throws IOException;
    
    public abstract void write(Object param1Object, DataOutputStream param1DataOutputStream) throws IOException;
    
    public abstract TagList<?> newListOfType();
  }
}
