package io.thunder.manager.packet;

/**
 * This is class is the better usage
 * of (de-)serializing Packet-Data
 *
 * You have to work with the {@link PacketBuffer}
 * and the {@link PacketReader} to (de-)serialize your
 * objects in the packet
 */
public abstract class BufferedPacket extends ThunderPacket {

    /**
     * Will be called if the packet will be send
     * You will add all the data to the given PacketBuffer
     *
     * @param buffer the PacketBuffer you append your Data to
     */
    public abstract void write(PacketBuffer buffer);

    /**
     * Will be called when the Packet gets created (when it's getting read)
     * Here you should set the values from the given PacketReader otherwise
     * the values (attributes) will be null, -1 or false (depends on the ParameterType)
     *
     * @param packetReader the PacketReader you get the Information from
     */
    public abstract void read(PacketReader packetReader);
}
