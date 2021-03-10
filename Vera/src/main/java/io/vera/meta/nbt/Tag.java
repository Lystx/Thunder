
package io.vera.meta.nbt;

import lombok.*;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ToString
@NotThreadSafe
@AllArgsConstructor
public class Tag {

    private static final Map.Entry<String, Tag> NULL_ENTRY = new AbstractMap.SimpleEntry<>(
            "", new Tag(Type.END, null));

    @Getter
    private final Type type;

    @Getter
    @Setter
    private Object object;

    public static Compound decode(DataInputStream stream) {
        Map.Entry<String, Tag> root = readFully(stream);
        return (Compound) root.getValue().getObject();
    }

    private static Map.Entry<String, Tag> readFully(DataInputStream stream) {
        try {
            int i = stream.readByte();
            Type type = Type.getMapping().get(i);
            if (type == Type.END) {
                return NULL_ENTRY;
            }

            int len = stream.readUnsignedShort();
            if (len > 0) {
                byte[] arr = new byte[len];
                stream.readFully(arr);
                String name = new String(arr, Type.UTF_8);
                Object o = type.read(name, stream);
                return new AbstractMap.SimpleEntry<>(name, new Tag(type, o));
            } else {
                Object o = type.read("", stream);
                return new AbstractMap.SimpleEntry<>("", new Tag(type, o));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Immutable
    public enum Type {
        END {
            @Override
            public Object read(String name, DataInputStream stream) {
                return null;
            }

            @Override
            public void write(Object o, DataOutputStream stream) {
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Void>(this);
            }
        },
        BYTE {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readByte();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeByte((byte) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Byte>(this);
            }
        },
        SHORT {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readShort();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeShort((short) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Short>(this);
            }
        },
        INT {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readInt();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeInt((int) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Integer>(this);
            }
        },
        LONG {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readLong();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeLong((long) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Long>(this);
            }
        },
        FLOAT {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readFloat();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeFloat((float) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Float>(this);
            }
        },
        DOUBLE {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                return stream.readDouble();
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                stream.writeDouble((double) o);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Double>(this);
            }
        },
        BYTE_ARRAY {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                int len = stream.readInt();
                byte[] arr = new byte[len];
                stream.readFully(arr);
                return arr;
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                byte[] arr = (byte[]) o;
                stream.writeInt(arr.length);
                for (byte b : arr) {
                    stream.writeByte(b);
                }
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<byte[]>(this);
            }
        },
        STRING {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                int len = stream.readUnsignedShort();
                byte[] arr = new byte[len];
                stream.readFully(arr);
                return new String(arr, UTF_8);
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                byte[] arr = ((String) o).getBytes(UTF_8);
                stream.writeShort(arr.length);
                for (byte b : arr) {
                    stream.writeByte(b);
                }
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<String>(this);
            }
        },
        LIST {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                int type = stream.readByte();
                Type t = mapping.get(type);
                if (t != null) {
                    int len = stream.readInt();
                    TagList tagList = t.newListOfType();
                    for (int i = 0; i < len; i++) {
                        tagList.add(t.read("", stream));
                    }

                    return tagList;
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                TagList tagList = (TagList) o;
                Type type = tagList.getType();
                stream.writeByte(type.ordinal());
                stream.writeInt(tagList.size());
                for (Object obj : tagList) {
                    type.write(obj, stream);
                }
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<TagList<?>>(this);
            }
        },
        COMPOUND {
            @Override
            public Object read(String name, DataInputStream stream) {
                Compound compound = new Compound(name);
                while (true) {
                    Map.Entry<String, Tag> tag = readFully(stream);
                    if (tag.getValue().getType() == Type.END) {
                        break;
                    } else {
                        compound.add(tag);
                    }
                }

                return compound;
            }

            @Override
            public void write(Object o, DataOutputStream stream) {
                Compound compound = (Compound) o;
                for (Map.Entry<String, Tag> entry : compound.getEntries().entrySet()) {
                    entry.getValue().getType().writeFully(entry.getKey(), entry.getValue().getObject(), stream);
                }
                END.writeFully("", null, stream);
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<Compound>(this);
            }
        },
        INT_ARRAY {
            @Override
            public Object read(String name, DataInputStream stream) throws IOException {
                int len = stream.readInt();
                int[] arr = new int[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = stream.readInt();
                }

                return arr;
            }

            @Override
            public void write(Object o, DataOutputStream stream) throws IOException {
                int[] arr = (int[]) o;
                stream.writeInt(arr.length);
                for (int i : arr) {
                    stream.writeInt(i);
                }
            }

            @Override
            public TagList<?> newListOfType() {
                return new TagList<int[]>(this);
            }
        };

        @Getter
        public static final Charset UTF_8 = StandardCharsets.UTF_8;

        @Getter
        private static final Map<Integer, Type> mapping = new HashMap<Integer, Type>() {
            @Override
            public Type get(Object key) {
                Type type = super.get(key);
                if (type == null) {
                    return END;
                }

                return type;
            }
        };

        static {
            for (Type t : Type.values()) {
                mapping.put(t.ordinal(), t);
            }
        }


        public void writeFully(String name, Object o, DataOutputStream stream) {
            try {
                stream.writeByte(this.ordinal());
                if (this == END) {
                    return;
                }

                byte[] arr = name.getBytes(UTF_8);
                stream.writeShort(arr.length);
                for (byte b : arr) {
                    stream.writeByte(b);
                }
                this.write(o, stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public abstract Object read(String name, DataInputStream stream) throws IOException;

        public abstract void write(Object o, DataOutputStream stream) throws IOException;

        public abstract TagList<?> newListOfType();
    }

}