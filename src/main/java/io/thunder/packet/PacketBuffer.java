package io.thunder.packet;

import io.thunder.impl.other.ProvidedPacketBuffer;
import io.thunder.utils.ThunderObject;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public interface PacketBuffer {

    /*
     * ================================================
     *          Writing Packets
     * ================================================
     */

    /**
     * Returns the OutPutStream to write data
     * @return outPutStream
     */
    ByteArrayOutputStream getByteArrayOutputStream();

    /**
     * Returns the DataOutPutStream to also write data
     * @return outPut
     */
    DataOutputStream getDataOutputStream();

    /**
     * Activates nullSafe to also be able to write null
     * Objects
     * @return current Buffer
     */
    PacketBuffer nullSafe();

    /**
     * Writes a String into the packet
     * @param s the text
     */
    void writeString(String s);

    /**
     * Writes bytes into the packet
     * @param b the byte-array
     */
    void writeBytes(byte[] b);

    /**
     * Writes a Boolean into the packet
     * @param b the boolean
     */
    void writeBoolean(boolean b);

    /**
     * Writes a Long into the packet
     * @param l the long
     */
    @SneakyThrows
    void writeLong(long l);

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uuid The uuid
     */
    void writeUUID(UUID uuid);

    /**
     * Automatically chooses the type of
     * the object and calls the given method
     * if none of the default types (Integer, String etc)
     * fits then  the {@link ProvidedPacketBuffer#writeObject(Object)}
     * will be called to serialize the object
     *
     * @param objects the Object(s)
     */
    void write(Object... objects);

    /**
     * Writes an Integer into the packet
     * @param i the int
     */
    void writeInt(int i);

    /**
     * Writes a Float into the packet
     * @param f the float
     */
    void writeFloat(float f);

    /**
     * Writes a Double into the packet
     * @param d the double
     */
    void writeDouble(double d);

    /**
     * Writes a single Byte into the packet
     * @param b the byte
     */
    void writeByte(byte b);

    /**
     * Writes a Short into the packet
     * @param s the short
     */
    void writeShort(short s);

    /**
     * Writes var int to buffer
     *
     * @param input the input
     */
    void writeVarInt(int input);

    /**
     * Writes a Enum to buffer
     *
     * @param e the enum
     */
    void writeEnum(Enum<?> e);

    /**
     * Writes an Object into the packet
     * @param object the Object
     */
    void writeObject(Object object);

    /**
     * Writes a {@link ThunderObject} to the current Buffer
     *
     * @param thunderObject the object
     */
    void writeThunderObject(ThunderObject thunderObject);


    /*
     * ================================================
     *          Reading Packets
     * ================================================
     */

    /**
     * Returns the InputStream to read Packetsd
     * @return inPutStream
     */
    DataInputStream getDataInputStream();

    /**
     * Reads a {@link ThunderObject} from Buffer
     * @param objectClass the genericClass
     * @return read object
     */
    <T extends ThunderObject>T readThunderObject(Class<T> objectClass);


    /**
     * Reads a Var Int
     *
     * @return int
     */
    int readVarInt();

    /**
     * Reads an enum value from the buffer
     *
     * @param enumClass The enum's class
     * @param <T>       The enum type
     * @return The enum object
     */
    <T extends Enum<T>> T readEnum(Class<T> enumClass);

    /**
     * Reads the current bytes
     * @return byte array
     */
    @SneakyThrows
    byte[] readBytes();

    /**
     * Reads the current UUID
     * @return current UUID
     */
    UUID readUUID();

    /**
     * Reads the current String
     * @return current String
     */
    String readString();

    /**
     * Reads the current byte
     * @return current byte
     */
    byte readByte();

    /**
     * Reads the current boolean
     * @return current boolean
     */
    boolean readBoolean();

    /**
     * Reads the current int
     * @return current int
     */
    int readInt();

    /**
     * Reads the current long
     * @return current long
     */
    long readLong();

    /**
     * Reads the current double
     * @return current double
     */
    double readDouble();

    /**
     * Reads the current float
     * @return current float
     */
    float readFloat();

    /**
     * Reads the current short
     * @return current short
     */
    short readShort();

    /**
     * Reads the current char
     * @return current char
     */
    char readChar();

    /**
     * This will read custom values
     * from InputStream (maybe serialized)
     *
     * @return Object from Stream
     */
    <T> T readObject();

    Object read(String type);

    void readFully(byte[] data);
}
