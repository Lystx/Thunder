package io.thunder.connection.codec;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.io.DataOutputStream;

public abstract class PacketEncoder extends PacketCodec {

    public abstract void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception;
}
