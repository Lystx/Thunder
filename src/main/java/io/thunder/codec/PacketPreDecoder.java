package io.thunder.codec;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


public abstract class PacketPreDecoder extends PacketCodec {

    public abstract Packet decode(PacketBuffer buf) throws Exception;
}
