

package io.thunder.manager.packet;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This class is used to read {@link Packet}s
 * and it's content.
 *
 */
@Getter
public class PacketReader {

    private final Packet packet;
    private final DataInputStream dataInputStream;

    /**
     * Constructs the current Reader from a
     * given Packet and sets the {@link java.io.InputStream}
     *
     * @param packet the given Packet
     */
    public PacketReader(Packet packet) {
        this.packet = packet;
        this.dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.getData()));
    }


    /**
     * Reads a Packet from {@link DataInputStream}
     *
     * @param dataInputStream the input
     * @return built packet
     * @throws IOException if something didn't work properly
     */
    public static Packet fromStream(DataInputStream dataInputStream) throws IOException {

        long time = dataInputStream.readLong();
        int dataLength = dataInputStream.readInt();
        byte[] data = new byte[dataLength];

        dataInputStream.readFully(data);
        Packet packet = new Packet(data);
        packet.setProcessingTime(time);
        return packet;
    }

    /**
     * Reads the current bytes
     * @return
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
     * This will someday read custom values
     * from InputStream (maybe serialized)
     *
     * @return Object from Stream
     */
    @Deprecated
    public <T> T readObject(Class<T> objectClass) {
        throw new UnsupportedOperationException("Not available yet");
    }

}
