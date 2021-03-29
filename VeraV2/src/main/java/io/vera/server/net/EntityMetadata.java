package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.vera.ui.chat.ChatComponent;
import io.vera.world.opt.BlockDirection;
import io.vera.world.other.Vector;
import io.vera.world.vector.AbstractVector;
import io.vson.manage.vson.VsonParser;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class EntityMetadata {
  private final List<EntityMetadataItem> items = Collections.synchronizedList(new ArrayList<>());
  
  public EntityMetadataItem get(int x) {
    return this.items.get(x);
  }
  
  public void add(EntityMetadataItem item) {
    this.items.add(item);
  }
  
  public void add(int index, EntityMetadataType type, Object value) {
    this.items.add(new EntityMetadataItem(index, type, new AtomicReference(type.cast(value))));
  }
  
  public void read(ByteBuf buf) {
    List<EntityMetadataItem> items = new ArrayList<>();
    short id;
    while ((id = buf.readUnsignedByte()) != 255) {
      float[] rd;
      int i;
      Vector vector;
      int bid;
      EntityMetadataType type = EntityMetadataType.values()[id];
      Object value = null;
      switch (type) {
        case BYTE:
          value = Byte.valueOf(buf.readByte());
          break;
        case VARINT:
          value = Integer.valueOf(NetData.rvint(buf));
          break;
        case FLOAT:
          value = Float.valueOf(buf.readFloat());
          break;
        case STRING:
          value = NetData.rstr(buf);
          break;
        case CHAT:
          try {
            value = ChatComponent.fromJson((new VsonParser()).parse(NetData.rstr(buf)).asVsonObject());
          } catch (IOException e) {
            e.printStackTrace();
          } 
          break;
        case SLOT:
          value = Slot.read(buf);
          break;
        case BOOLEAN:
          value = Boolean.valueOf(buf.readBoolean());
          break;
        case ROTATION:
          rd = new float[3];
          for (i = 0; i < 3; i++)
            rd[i] = buf.readFloat(); 
          value = new Vector(rd[0], rd[1], rd[2]);
          break;
        case POSITION:
          vector = new Vector();
          NetData.rvec(buf, (AbstractVector<?>)vector);
          value = vector;
          break;
        case OPTPOSITION:
          if (buf.readBoolean()) {
            vector = new Vector();
            NetData.rvec(buf, (AbstractVector<?>)vector);
            value = vector;
            break;
          } 
          value = null;
          break;
        case DIRECTION:
          value = BlockDirection.fromMinecraftDirection(NetData.rvint(buf));
          break;
        case OPTUUID:
          if (buf.readBoolean()) {
            value = new UUID(buf.readLong(), buf.readLong());
            break;
          } 
          value = null;
          break;
        case BLOCKID:
          bid = NetData.rvint(buf);
          value = new int[] { bid >> 4 & 0xF, bid & 0xF };
          break;
        default:
          continue;
      } 
      items.add(new EntityMetadataItem(id, type, new AtomicReference(value)));
    } 
    this.items.clear();
    this.items.addAll(items);
  }
  
  public void write(ByteBuf buf) {
    List<EntityMetadataItem> items = this.items;
    for (EntityMetadataItem item : items) {
      String str;
      ChatComponent cc;
      Slot slot;
      Vector rv, pv, opv;
      BlockDirection direction;
      UUID uuid;
      int[] blockIdData;
      buf.writeByte(item.index);
      buf.writeByte(item.type.id);
      switch (item.type) {
        case BYTE:
          buf.writeByte(item.asByte());
        case VARINT:
          NetData.wvint(buf, item.asInt());
        case FLOAT:
          buf.writeFloat(item.asFloat());
        case STRING:
          str = Objects.<String>requireNonNull(item.asString(), "string required but was null (idx: " + item.index + ")");
          NetData.wstr(buf, str);
        case CHAT:
          cc = Objects.<ChatComponent>requireNonNull(item.asChatComponent(), "chat component required but was null (idx: " + item.index + ")");
          NetData.wstr(buf, cc.toString());
        case SLOT:
          slot = Objects.<Slot>requireNonNull(item.asSlot(), "slot required but was null (idx: " + item.index + ")");
          slot.write(buf);
        case BOOLEAN:
          buf.writeBoolean(item.asBoolean());
        case ROTATION:
          rv = Objects.<Vector>requireNonNull(item.asRotation(), "rotation required but was null (idx: " + item.index + ")");
          buf.writeFloat((float)rv.getX());
          buf.writeFloat((float)rv.getY());
          buf.writeFloat((float)rv.getZ());
        case POSITION:
          pv = Objects.<Vector>requireNonNull(item.asPosition(), "position required but was null (idx: " + item.index + ")");
          NetData.wvec(buf, (AbstractVector<?>)pv);
        case OPTPOSITION:
          opv = item.asPosition();
          buf.writeBoolean((opv != null));
          if (opv != null)
            NetData.wvec(buf, (AbstractVector<?>)opv); 
        case DIRECTION:
          direction = Objects.<BlockDirection>requireNonNull(item.asDirection(), "direction required but was null (idx: " + item.index + ")");
          NetData.wvint(buf, direction.getMinecraftDirection());
        case OPTUUID:
          uuid = item.asUUID();
          buf.writeBoolean((uuid != null));
          if (uuid != null) {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
          } 
        case BLOCKID:
          blockIdData = item.asBlockId();
          NetData.wvint(buf, blockIdData[0] << 4 | blockIdData[1]);
      } 
    } 
    buf.writeByte(255);
  }
  
  public static class EntityMetadataItem {
    private final int index;
    
    private final EntityMetadataType type;
    
    private final AtomicReference<Object> value;
    
    @ConstructorProperties({"index", "type", "value"})
    public EntityMetadataItem(int index, EntityMetadataType type, AtomicReference<Object> value) {
      this.index = index;
      this.type = type;
      this.value = value;
    }
    
    public int getIndex() {
      return this.index;
    }
    
    public EntityMetadataType getType() {
      return this.type;
    }
    
    public AtomicReference<Object> getValue() {
      return this.value;
    }
    
    public void set(Object value) {
      this.value.set(this.type.cast(value));
    }
    
    public byte asByte() {
      Object object = this.value.get();
      return (object instanceof Number) ? ((Number)object).byteValue() : 0;
    }
    
    public int asInt() {
      Object object = this.value.get();
      return (object instanceof Number) ? ((Number)object).intValue() : 0;
    }
    
    public float asFloat() {
      Object object = this.value.get();
      return (object instanceof Number) ? ((Number)object).floatValue() : 0.0F;
    }
    
    public boolean asBit(int x) {
      return ((asByte() & 1 << x) != 0);
    }
    
    public void setBit(int x, boolean value) {
      byte val;
      byte newVal;
      do {
        val = newVal = asByte();
        if ((((val & 1 << x) != 0)) == value)
          return; 
        if (value) {
          newVal = (byte)(newVal | 1 << x);
        } else {
          newVal = (byte)(newVal & (1 << x ^ 0xFFFFFFFF));
        } 
      } while (!this.value.compareAndSet(Byte.valueOf(val), Byte.valueOf(newVal)));
    }
    
    public String asString() {
      Object object = this.value.get();
      return String.valueOf(object);
    }
    
    public ChatComponent asChatComponent() {
      return (ChatComponent)this.value.get();
    }
    
    public boolean asBoolean() {
      Object object = this.value.get();
      return ((object instanceof Boolean) ? (Boolean)object : Boolean.valueOf(String.valueOf(object))).booleanValue();
    }
    
    public Slot asSlot() {
      return (Slot)this.value.get();
    }
    
    public Vector asRotation() {
      return (Vector)this.value.get();
    }
    
    public Vector asPosition() {
      return (Vector)this.value.get();
    }
    
    public BlockDirection asDirection() {
      return (BlockDirection)this.value.get();
    }
    
    public UUID asUUID() {
      return (UUID)this.value.get();
    }
    
    public int[] asBlockId() {
      return (int[])this.value.get();
    }
  }
  
  public enum EntityMetadataType {
    BYTE(0) {
      public Object cast(Object object) {
        return Byte.valueOf((object == null) ? 0 : ((Number)object).byteValue());
      }
    },
    VARINT(1) {
      public Object cast(Object object) {
        return Integer.valueOf((object == null) ? 0 : ((Number)object).intValue());
      }
    },
    FLOAT(2) {
      public Object cast(Object object) {
        return Float.valueOf((object == null) ? 0.0F : ((Number)object).floatValue());
      }
    },
    STRING(3) {
      public Object cast(Object object) {
        return (object == null) ? "" : object.toString();
      }
    },
    CHAT(4) {
      public Object cast(Object object) {
        return (object == null) ? new ChatComponent() : object.toString();
      }
    },
    SLOT(5) {
      public Object cast(Object object) {
        return object;
      }
    },
    BOOLEAN(6) {
      public Object cast(Object object) {
        return Boolean.valueOf((object instanceof Boolean) ? ((Boolean)object).booleanValue() : Boolean.parseBoolean(String.valueOf(object)));
      }
    },
    ROTATION(7) {
      public Object cast(Object object) {
        return object;
      }
    },
    POSITION(8) {
      public Object cast(Object object) {
        return object;
      }
    },
    OPTPOSITION(9) {
      public Object cast(Object object) {
        return object;
      }
    },
    DIRECTION(10) {
      public Object cast(Object object) {
        return object;
      }
    },
    OPTUUID(11) {
      public Object cast(Object object) {
        return object;
      }
    },
    BLOCKID(12) {
      public Object cast(Object object) {
        return object;
      }
    };
    
    private final int id;
    
    EntityMetadataType(int id) {
      this.id = id;
    }
    
    public abstract Object cast(Object param1Object);
  }
}
