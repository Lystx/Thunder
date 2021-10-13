

package io.thunder.impl.other;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import io.thunder.utils.ThunderUtils;
import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.annotation.other.Vson;
import io.thunder.utils.vson.enums.FileFormat;
import io.thunder.utils.vson.manage.vson.VsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * This class is used to read and write values to a {@link Packet}
 */
@Getter
public class ProvidedPacketBuffer implements PacketBuffer {

    /**
     * The byteOutPut
     */
    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * The dataOutput
     */
    private DataOutputStream dataOutputStream;

    /**
     * If nullsafe is activated
     */
    private boolean nullSafe;

    /**
     * Prepares all values
     */
    ProvidedPacketBuffer() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        this.nullSafe = false;
    }

    /**
     * Returns a new PacketBuffer
     *
     * @return new instance of buffer
     */
    public static PacketBuffer newInstance() {
        return new ProvidedPacketBuffer();
    }


    /**
     * Activates nullSafe
     * @return
     */
    public ProvidedPacketBuffer nullSafe() {
        this.nullSafe = true;
        return this;
    }

    private boolean checkNullSafe(Object object) {
        if (this.nullSafe) {
            this.nullSafe = false;
            if (object == null) {
                this.writeString("_null_");
            } else {
                this.writeString("_allRight_");
            }

            return object == null;
        }
        return false;
    }

    /**
     * Writes a String into the packet
     * @param s the text
     */
    public synchronized void writeString(String s) {
        if (!this.checkNullSafe(s)) {
            this.writeBytes(s.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Writes bytes into the packet
     * @param b the byte-array
     */
    @SneakyThrows
    public synchronized void writeBytes(byte[] b) {
        if (!this.checkNullSafe(b)) {
            dataOutputStream.writeInt(b.length);
            dataOutputStream.write(b);
        }
    }

    /**
     * Writes a Boolean into the packet
     * @param b the boolean
     */
    @SneakyThrows
    public synchronized void writeBoolean(boolean b) {
        if (!this.checkNullSafe(b)) {
            dataOutputStream.writeBoolean(b);
        }
    }

    /**
     * Writes a Long into the packet
     * @param l the long
     */
    @SneakyThrows
    public synchronized void writeLong(long l) {
        if (!this.checkNullSafe(l)) {
            dataOutputStream.writeLong(l);
        }
    }

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uuid The uuid
     */
    public synchronized void writeUUID(UUID uuid) {
        if (!this.checkNullSafe(uuid)) {
            this.writeLong(uuid.getMostSignificantBits());
            this.writeLong(uuid.getLeastSignificantBits());
        }
    }

    /**
     * Automatically chooses the type of
     * the object and calls the given method
     * if none of the default types (Integer, String etc)
     * fits then  the {@link ProvidedPacketBuffer#writeObject(Object)}
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
        if (!this.checkNullSafe(i)) {
            dataOutputStream.writeInt(i);
        }
    }

    /**
     * Writes a Float into the packet
     * @param f the float
     */
    @SneakyThrows
    public synchronized void writeFloat(float f) {
        if (!this.checkNullSafe(f)) {
            dataOutputStream.writeFloat(f);
        }
    }

    /**
     * Writes a Double into the packet
     * @param d the double
     */
    @SneakyThrows
    public synchronized void writeDouble(double d) {
        if (!this.checkNullSafe(d)) {
            dataOutputStream.writeDouble(d);
        }
    }

    /**
     * Writes a single Byte into the packet
     * @param b the byte
     */
    @SneakyThrows
    public synchronized void writeByte(byte b) {
        if (!this.checkNullSafe(b)) {
            dataOutputStream.writeByte(b);
        }
    }

    /**
     * Writes a Short into the packet
     * @param s the short
     */
    @SneakyThrows
    public synchronized void writeShort(short s) {
        if (!this.checkNullSafe(s)) {
            dataOutputStream.writeShort(s);
        }
    }

    /**
     * Writes var int to buffer
     *
     * @param input the input
     */
    public synchronized void writeVarInt(int input) {
        if (!this.checkNullSafe(input)) {
            while ((input & -128) != 0) {
                this.writeInt(input & 127 | 128);
                input >>>= 7;
            }

            this.writeInt(input);
        }
    }

    /**
     * Writes a Enum to buffer
     *
     * @param e the enum
     */
    public synchronized void writeEnum(Enum<?> e) {
        if (!this.checkNullSafe(e)) {
            this.writeString(e.name());
        }
    }


    /**
     * Writes an Object into the packet
     * @param object the Object
     */
    public synchronized void writeObject(Object object) {
        if (!this.checkNullSafe(object)) {
            this.writeString(object.getClass().getName());
            VsonValue parse = Vson.get().parse(object);
            this.writeString(parse.toString(FileFormat.RAW_JSON));
        }
    }

    /**
     * Writes a {@link ThunderObject} to the current Buffer
     *
     * @param thunderObject the object
     */
    public void writeThunderObject(ThunderObject thunderObject) {
        if (!this.checkNullSafe(thunderObject)) {
            thunderObject.write(this);
        }
    }

    /**
     * Reads a {@link ThunderObject} from Buffer
     * @param objectClass the genericClass
     * @return read object
     */
    @SneakyThrows
    public <T extends ThunderObject>T readThunderObject(Class<T> objectClass) {
        T t = ThunderUtils.getInstance(objectClass);
        if (t != null) {
            t.read(this);
        }
        return t;
    }

    private DataInputStream dataInputStream;


    public static PacketBuffer newInstance(Packet packet) {
        return new ProvidedPacketBuffer(packet);
    }
    public static PacketBuffer newInstance(DataInputStream dataInputStream) {
        return new ProvidedPacketBuffer(dataInputStream);
    }

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link InputStream}
     *
     * @param packet the given Packet
     */
    ProvidedPacketBuffer(Packet packet) {
        this(new DataInputStream(new ByteArrayInputStream(packet.getData())));
    }

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link InputStream}
     *
     * @param dataInputStream the given DataInputStream
     */
    ProvidedPacketBuffer(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    /**
     * Reads a Var Int
     *
     * @return int
     */
    public synchronized int readVarInt() {
        if (checkNullSafe()) {
            return -1;
        }
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
        if (checkNullSafe()) {
            return null;
        }
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
        if (checkNullSafe()) {
            return null;
        }
        return new UUID(readLong(), readLong());
    }


    /**
     * Checks if nullSafe is active
     * if its active and value is null
     * you can return null
     * else you can return the not-null-value
     *
     * @return boolean
     */
    private boolean checkNullSafe() {
        if (this.nullSafe) {
            this.nullSafe = false;
            try {
                String s = readString();
                return s.equals("_null_");
            } catch (Exception e) {
                //Ignoring
            }
        }
        return false;
    }

    /**
     * Reads the current String
     * @return current String
     */
    public synchronized String readString() {
        if (checkNullSafe()) {
            return null;
        }
        return new String(this.readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads the current byte
     * @return current byte
     */
    @SneakyThrows
    public synchronized byte readByte() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readByte();
    }

    /**
     * Reads the current boolean
     * @return current boolean
     */
    @SneakyThrows
    public synchronized boolean readBoolean() {
        if (checkNullSafe()) {
            return false;
        }
        return dataInputStream.readBoolean();
    }

    /**
     * Reads the current int
     * @return current int
     */
    @SneakyThrows
    public synchronized int readInt() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readInt();
    }

    /**
     * Reads the current long
     * @return current long
     */
    @SneakyThrows
    public synchronized long readLong() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readLong();
    }

    /**
     * Reads the current double
     * @return current double
     */
    @SneakyThrows
    public synchronized double readDouble() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readDouble();
    }

    /**
     * Reads the current float
     * @return current float
     */
    @SneakyThrows
    public synchronized float readFloat() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readFloat();
    }

    /**
     * Reads the current short
     * @return current short
     */
    @SneakyThrows
    public synchronized short readShort() {
        if (checkNullSafe()) {
            return -1;
        }
        return dataInputStream.readShort();
    }

    /**
     * Reads the current char
     * @return current char
     */
    @SneakyThrows
    public synchronized char readChar() {
        if (checkNullSafe()) {
            return 'N';
        }
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
        if (checkNullSafe()) {
            return null;
        }
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
    /**
     * See the general contract of the <code>readFully</code>
     * method of <code>DataInput</code>.
     * <p>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @param      data   the buffer into which the data is read.
     * @exception  EOFException  if this input stream reaches the end before
     *             reading all the bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        FilterInputStream#in
     */
    @SneakyThrows
    public void readFully(byte[] data) {
        this.dataInputStream.readFully(data);
    }
}
