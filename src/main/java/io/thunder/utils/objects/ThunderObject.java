package io.thunder.utils.objects;

import io.thunder.packet.PacketBuffer;

/**
 * This class is used to declare certain Object
 * as ThunderObjects and (de-)serialize them into {@link PacketBuffer}
 */
public interface ThunderObject {

    /**
     * When the Object gets written into the PacketBuffer
     * here you copy all the data of the packet to the buffer
     * by writing values like Strings, int, etc
     *
     * @param buf the packetBuffer
     */
    void write(PacketBuffer buf);

    /**
     * When the Object gets read from the PacketBuffer
     * here you set all the fields of the packet from the buffer
     * by reading values like Strings, int, etc
     *
     * @param buf the packetBuffer
     */
    void read(PacketBuffer buf);
}
