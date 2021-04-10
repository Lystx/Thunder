

package io.thunder.manager.packet;

import io.thunder.Thunder;
import io.thunder.manager.utils.LogLevel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * This class is used to build a raw {@link Packet}
 *
 * You have to provide a packetID in the constructor
 * and then you can write what you want in the packet
 * with the differnt methods down below
 * and then build it together
 * */
@Getter
public class PacketBuffer {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final DataOutputStream dataOutputStream;

    private final short packetID;

    /**
     * Prepares all values
     * @param packetId the ID for the packet
     */
    public PacketBuffer(int packetId) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        this.packetID = (short) packetId;
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
     * Writes an Object into the packet
     * @param o the Object
     * @return current PacketBuffer
     */
    @Deprecated
    public synchronized PacketBuffer writeObject(Object o) {
        throw new UnsupportedOperationException("Not included at this point");
    }

    /**
     * This will build the {@link Packet}
     * together and closes the {@link DataOutputStream}
     *
     * @return the built Packet
     */
    public synchronized Packet build() {

        try {
            dataOutputStream.close();
        } catch (IOException e) {
            Thunder.LOGGER.log(LogLevel.ERROR, "Packet couldn't be built (" + getClass().getSimpleName() + ")");
        }

        return new Packet(packetID, byteArrayOutputStream.toByteArray());
    }
}
