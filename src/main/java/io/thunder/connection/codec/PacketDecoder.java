package io.thunder.connection.codec;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


public abstract class PacketDecoder extends PacketCodec {

    public abstract Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection) throws Exception;
}
