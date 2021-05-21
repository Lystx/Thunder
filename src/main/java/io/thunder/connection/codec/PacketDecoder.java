package io.thunder.connection.codec;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


public abstract class PacketDecoder extends PacketCodec {

    /**
     * Decodes a Packet
     *
     * @param packet the packet to decode
     * @param buf the buf to work with
     * @param thunderConnection the connection it came from
     * @param _class the name of the class
     * @return decoded Packet
     * @throws Exception if something goes wrong
     */
    public abstract Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection, String _class) throws Exception;
}
