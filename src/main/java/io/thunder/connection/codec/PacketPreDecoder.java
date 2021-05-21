package io.thunder.connection.codec;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


public abstract class PacketPreDecoder extends PacketCodec {

    /**
     * Pre-Decodes a Packet from a Buffer
     *
     * @param buf the buffer
     * @return Packet
     * @throws Exception if something goes wrong
     */
    public abstract Packet decode(PacketBuffer buf) throws Exception;
}
