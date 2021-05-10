

package io.thunder.packet;

import io.thunder.Thunder;
import io.thunder.manager.logger.LogLevel;
import io.vson.annotation.other.Vson;
import io.vson.manage.vson.VsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to build
 *
 * You can write what you want in the packet
 * with the differnt methods down below
 * and then build it together
 * */
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
     * @return current PacketBuffer
     */
    public synchronized PacketBuffer writeString(String s) {
        return this.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes bytes into the packet
     * @param b the byte-array
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeBytes(byte[] b) {
        dataOutputStream.writeInt(b.length);
        dataOutputStream.write(b);
        return this;
    }

    /**
     * Writes a Boolean into the packet
     * @param b the boolean
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeBoolean(boolean b) {
        dataOutputStream.writeBoolean(b);
        return this;
    }

    /**
     * Writes a Long into the packet
     * @param l the long
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeLong(long l) {
        dataOutputStream.writeLong(l);
        return this;
    }

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uuid The uuid
     * @return This
     */
    public PacketBuffer writeUUID(UUID uuid) {
        if (uuid == null) {
            return this;
        }
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
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
    public void write(Object... objects) {
        for (Object o : objects) {
            if (o.getClass().equals(Integer.class) || o.getClass().equals(int.class)) {
                this.writeInt((Integer) o);
            } else if (o.getClass().equals(Double.class) || o.getClass().equals(double.class)) {
                this.writeDouble((Double) o);
            } else if (o.getClass().equals(String.class)) {
                this.writeString((String) o);
            } else if (o.getClass().equals(Boolean.class) || o.getClass().equals(boolean.class)) {
                this.writeBoolean((Boolean) o);
            } else if (o.getClass().equals(UUID.class)) {
                this.writeUUID((UUID) o);
            } else if (o.getClass().equals(Enum.class)) {
                this.writeEnum((Enum<?>) o);
            } else if (o instanceof List) {
                final List<?> o1 = (List<?>) o;
                if (o1.isEmpty()) {
                    return;
                }
                if (o1.get(0) instanceof String) {
                    this.writeList((List<String>) o1);
                }
            } else {
                this.writeObject(o);
            }
        }
    }

    /**
     * Writes an Integer into the packet
     * @param i the int
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeInt(int i) {
        dataOutputStream.writeInt(i);
        return this;
    }

    /**
     * Writes a Float into the packet
     * @param f the float
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeFloat(float f) {
        dataOutputStream.writeFloat(f);
        return this;
    }

    /**
     * Writes a Double into the packet
     * @param d the double
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeDouble(double d) {
        dataOutputStream.writeDouble(d);
        return this;
    }

    /**
     * Writes a single Byte into the packet
     * @param b the byte
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeByte(byte b) {
        dataOutputStream.writeByte(b);
        return this;
    }

    /**
     * Writes a Short into the packet
     * @param s the short
     * @return current PacketBuffer
     */
    @SneakyThrows
    public synchronized PacketBuffer writeShort(short s) {
        dataOutputStream.writeShort(s);
        return this;
    }

    /**
     * Writes var int to buffer
     *
     * @param input the input
     * @return current PacketBuffer
     */
    public synchronized PacketBuffer writeVarInt(int input) {
        while((input & -128) != 0){
            this.writeInt(input & 127 | 128);
            input >>>= 7;
        }

        this.writeInt(input);
        return this;
    }

    /**
     * Writes a Enum to buffer
     *
     * @param e the enum
     * @return current PacketBuffer
     */
    public synchronized PacketBuffer writeEnum(Enum<?> e) {
        return this.writeString(e.name());
    }

    /**
     * Writes a stringList into the database
     *
     * @param list the list
     * @return current PacketBuffer
     */
    public PacketBuffer writeList(List<?> list) {
        this.writeVarInt(list.size());
        list.forEach(o -> this.writeString(o.toString()));
        return this;
    }

    /**
     * Writes an Object into the packet
     * @param o the Object
     * @return current PacketBuffer
     */
    @Deprecated
    public synchronized PacketBuffer writeObject(Object o) {
        return this.writeString(Vson.get().parse(o).toString());
    }

    /**
     * This will build
     * together and closes the {@link DataOutputStream}
     *
     * @return the built Packet
     */
    public synchronized byte[] build() {

        try {
            dataOutputStream.close();
        } catch (IOException e) {
            Thunder.LOGGER.log(LogLevel.ERROR, "Packet couldn't be built (" + getClass().getSimpleName() + ")");
        }

        return byteArrayOutputStream.toByteArray();
    }


    private Packet packet;
    private DataInputStream dataInputStream;

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link java.io.InputStream}
     *
     * @param packet the given Packet
     */
    public PacketBuffer(Packet packet) {
        this.packet = packet;
        this.dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.getData()));
    }


    /**
     * Reads a Var Int
     *
     * @return int
     */
    public int readVarInt() {
        int i = 0;
        int j = 0;

        while(true){
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
    public <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        return Enum.valueOf(enumClass, readString());
    }

    /**
     * Reads the current bytes
     * @return byte array
     */
    @SneakyThrows
    public byte[] readBytes() {
        final int dataLength = dataInputStream.readInt();
        final byte[] data = new byte[dataLength];

        final int dataRead = dataInputStream.read(data, 0, dataLength);
        if (dataRead != dataLength) throw new IOException("Not enough data available");
        return data;
    }

    /**
     * Reads the current UUID
     * @return current UUID
     */
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Reads the current String
     * @return current String
     */
    public String readString() {
        return new String(this.readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads the current byte
     * @return current byte
     */
    @SneakyThrows
    public byte readByte() {
        return dataInputStream.readByte();
    }

    /**
     * Reads the current boolean
     * @return current boolean
     */
    @SneakyThrows
    public boolean readBoolean() {
        return dataInputStream.readBoolean();
    }

    /**
     * Reads the current int
     * @return current int
     */
    @SneakyThrows
    public int readInt() {
        return dataInputStream.readInt();
    }

    /**
     * Reads the current long
     * @return current long
     */
    @SneakyThrows
    public long readLong() {
        return dataInputStream.readLong();
    }

    /**
     * Reads the current double
     * @return current double
     */
    @SneakyThrows
    public double readDouble() {
        return dataInputStream.readDouble();
    }

    /**
     * Reads the current float
     * @return current float
     */
    @SneakyThrows
    public float readFloat() {
        return dataInputStream.readFloat();
    }

    /**
     * Reads the current short
     * @return current short
     */
    @SneakyThrows
    public short readShort() {
        return dataInputStream.readShort();
    }

    /**
     * Reads the current char
     * @return current char
     */
    @SneakyThrows
    public char readChar() {
        return dataInputStream.readChar();
    }

    /**
     * Reads the current UTF
     * @return current UTF
     */
    @SneakyThrows
    public String readUTF() {
        return dataInputStream.readUTF();
    }

    /**
     * This will read custom values
     * from InputStream (maybe serialized)
     *
     * @return Object from Stream
     */
    @Deprecated @SneakyThrows
    public <T> T readObject(Class<T> objectClass) {
        return Vson.get().unparse(new VsonParser(this.readString()).parse(), objectClass);
    }


}
