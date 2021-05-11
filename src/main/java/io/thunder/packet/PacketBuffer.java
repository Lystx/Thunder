

package io.thunder.packet;

import com.google.gson.JsonIOException;
import io.vson.VsonValue;
import io.vson.annotation.other.Vson;
import io.vson.enums.FileFormat;
import io.vson.manage.vson.VsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to read and write values to a {@link Packet}
 */
@Getter
public class PacketBuffer {

    private ByteArrayOutputStream byteArrayOutputStream;
    private DataOutputStream dataOutputStream;

    /**
     * Prepares all values
     */
    public PacketBuffer() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    /**
     * Writes a String into the packet
     * @param s the text
     */
    public synchronized void writeString(String s) {
        this.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes bytes into the packet
     * @param b the byte-array
     */
    @SneakyThrows
    public synchronized void writeBytes(byte[] b) {
        dataOutputStream.writeInt(b.length);
        dataOutputStream.write(b);
    }

    /**
     * Writes a Boolean into the packet
     * @param b the boolean
     */
    @SneakyThrows
    public synchronized void writeBoolean(boolean b) {
        dataOutputStream.writeBoolean(b);
    }

    /**
     * Writes a Long into the packet
     * @param l the long
     */
    @SneakyThrows
    public synchronized void writeLong(long l) {
        dataOutputStream.writeLong(l);
    }

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uuid The uuid
     */
    public synchronized void writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Automatically chooses the type of
     * the object and calls the given method
     * if none of the default types (Integer, String etc)
     * fits then  the {@link PacketBuffer#writeObject(Object)}
     * will be called to serialize the object
     *
     * @param objects the Object(s)
     */
    public synchronized void write(Object... objects) {
        for (Object o : objects) {
            if (o.getClass().equals(Integer.class) || o.getClass().equals(int.class)) {
                this.writeInt((Integer) o);
            } else if (o.getClass().equals(Double.class) || o.getClass().equals(double.class)) {
                this.writeDouble((Double) o);
            } else if (o.getClass().equals(String.class)) {
                this.writeString((String) o);
            } else if (o.getClass().equals(Boolean.class) || o.getClass().equals(boolean.class)) {
                this.writeBoolean((Boolean) o);
            } else if (o instanceof UUID) {
                this.writeUUID((UUID) o);
            } else if (o instanceof Enum) {
                this.writeEnum((Enum<?>) o);
            } else if (o instanceof List) {
                final List<?> o1 = (List<?>) o;
                if (o1.isEmpty()) {
                    return;
                }
                if (o1.get(0) instanceof String) {
                    this.writeList(o1);
                }
            } else {
                this.writeObject(o);
            }
        }
    }

    /**
     * Writes an Integer into the packet
     * @param i the int
     */
    @SneakyThrows
    public synchronized void writeInt(int i) {
        dataOutputStream.writeInt(i);
    }

    /**
     * Writes a Float into the packet
     * @param f the float
     */
    @SneakyThrows
    public synchronized void writeFloat(float f) {
        dataOutputStream.writeFloat(f);
    }

    /**
     * Writes a Double into the packet
     * @param d the double
     */
    @SneakyThrows
    public synchronized void writeDouble(double d) {
        dataOutputStream.writeDouble(d);
    }

    /**
     * Writes a single Byte into the packet
     * @param b the byte
     */
    @SneakyThrows
    public synchronized void writeByte(byte b) {
        dataOutputStream.writeByte(b);
    }

    /**
     * Writes a Short into the packet
     * @param s the short
     */
    @SneakyThrows
    public synchronized void writeShort(short s) {
        dataOutputStream.writeShort(s);
    }

    /**
     * Writes var int to buffer
     *
     * @param input the input
     */
    public synchronized void writeVarInt(int input) {
        while((input & -128) != 0){
            this.writeInt(input & 127 | 128);
            input >>>= 7;
        }

        this.writeInt(input);
    }

    /**
     * Writes a Enum to buffer
     *
     * @param e the enum
     */
    public synchronized void writeEnum(Enum<?> e) {
        this.writeString(e.name());
    }

    /**
     * Writes a stringList into the database
     *
     * @param list the list
     */
    public synchronized void writeList(List<?> list) {
        this.writeVarInt(list.size());
        list.forEach(o -> this.writeString(o.toString()));
    }

    /**
     * Writes an Object into the packet
     * @param object the Object
     */
    public synchronized void writeObject(Object object) {
        this.writeString(object.getClass().getName());
        VsonValue parse = Vson.get().parse(object);
        this.writeString(parse.toString(FileFormat.RAW_JSON));
    }

    /**
     * Writes all the Fields of the Class into the {@link PacketBuffer}
     * automatically (Might choose the wrong write Option)
     * So its always better to do it manually!
     *
     * @param classObject the object of the class Mostly [ buf.writeClass(this) ]
     */
    @SneakyThrows
    public synchronized void writeClass(Object classObject) {
        for (Field declaredField : classObject.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            Object o = declaredField.get(classObject);
            this.write(o);
        }
    }

    private DataInputStream dataInputStream;

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link java.io.InputStream}
     *
     * @param packet the given Packet
     */
    public PacketBuffer(Packet packet) {
        this(new DataInputStream(new ByteArrayInputStream(packet.getData())));
    }

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link java.io.InputStream}
     *
     * @param dataInputStream the given DataInputStream
     */
    public PacketBuffer(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    /**
     * Reads a Var Int
     *
     * @return int
     */
    public synchronized int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;

            if(j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    /**
     * Reads an enum value from the buffer
     *
     * @param enumClass The enum's class
     * @param <T>       The enum type
     * @return The enum object
     */
    public synchronized <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        return Enum.valueOf(enumClass, readString());
    }

    /**
     * Reads the current bytes
     * @return byte array
     */
    @SneakyThrows
    public synchronized byte[] readBytes() {
        int dataLength = dataInputStream.readInt();
        byte[] data = new byte[dataLength];

        int dataRead = dataInputStream.read(data, 0, dataLength);
        if (dataRead != dataLength) {
            throw new IOException("Not enough data available");
        }
        return data;
    }

    /**
     * Reads the current UUID
     * @return current UUID
     */
    public synchronized UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Reads the current String
     * @return current String
     */
    public synchronized String readString() {
        return new String(this.readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads the current byte
     * @return current byte
     */
    @SneakyThrows
    public synchronized byte readByte() {
        return dataInputStream.readByte();
    }

    /**
     * Reads the current boolean
     * @return current boolean
     */
    @SneakyThrows
    public synchronized boolean readBoolean() {
        return dataInputStream.readBoolean();
    }

    /**
     * Reads the current int
     * @return current int
     */
    @SneakyThrows
    public synchronized int readInt() {
        return dataInputStream.readInt();
    }

    /**
     * Reads the current long
     * @return current long
     */
    @SneakyThrows
    public synchronized long readLong() {
        return dataInputStream.readLong();
    }

    /**
     * Reads the current double
     * @return current double
     */
    @SneakyThrows
    public synchronized double readDouble() {
        return dataInputStream.readDouble();
    }

    /**
     * Reads the current float
     * @return current float
     */
    @SneakyThrows
    public synchronized float readFloat() {
        return dataInputStream.readFloat();
    }

    /**
     * Reads the current short
     * @return current short
     */
    @SneakyThrows
    public synchronized short readShort() {
        return dataInputStream.readShort();
    }

    /**
     * Reads the current char
     * @return current char
     */
    @SneakyThrows
    public synchronized char readChar() {
        return dataInputStream.readChar();
    }


    /**
     * This will read custom values
     * from InputStream (maybe serialized)
     *
     * @return Object from Stream
     */
    @SneakyThrows
    public synchronized <T> T readObject() {
        String objectClass = this.readString();
        return Vson.get().unparse(new VsonParser(this.readString()).parse(), (Class<T>) Class.forName(objectClass));
    }

    public synchronized Object read(String type) {
        Object object;
        if (type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int")) {
            object = this.readInt();
        } else if (type.equalsIgnoreCase("double")) {
            object = this.readDouble();
        } else if (type.equalsIgnoreCase("short")) {
            object = this.readShort();
        } else if (type.equalsIgnoreCase("long")) {
            object = this.readLong();
        } else if (type.equalsIgnoreCase("float")) {
            object = this.readFloat();
        } else if (type.equalsIgnoreCase("byte")) {
            object = this.readByte();
        } else if (type.equalsIgnoreCase("boolean")) {
            object = this.readBoolean();
        } else if (type.equalsIgnoreCase("uuid")) {
            object = this.readUUID();
        } else if (type.equalsIgnoreCase("char")) {
            object = this.readChar();
        } else if (type.equalsIgnoreCase("string")) {
            object = this.readString();
        } else {
            object = readObject();
        }
        return object;
    }

    @SneakyThrows
    public void readFully(byte[] data) {
        this.dataInputStream.readFully(data);
    }
}
