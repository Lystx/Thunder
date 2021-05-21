package io.thunder.connection.codec;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.io.DataOutputStream;

public abstract class PacketEncoder extends PacketCodec {

    /**
     * Encodes a Packet
     * @param packet the packet to encode
     * @param dataOutputStream the output to transfer the data to
     * @param buf the buf to work with
     * @throws Exception if something goes wrong
     */
    public abstract void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception;
}
