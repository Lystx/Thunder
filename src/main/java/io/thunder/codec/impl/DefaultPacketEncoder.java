package io.thunder.codec.impl;

import io.thunder.codec.PacketEncoder;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.io.DataOutputStream;

public class DefaultPacketEncoder extends PacketEncoder {

    @Override
    public void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception {

        buf.writeString(packet.getClass().getName());
        packet.write(buf);
        packet.setData(buf.build());

        dataOutputStream.writeLong(System.currentTimeMillis());
        dataOutputStream.writeInt(packet.getProtocolId());
        dataOutputStream.writeInt(packet.getProtocolVersion());
        dataOutputStream.writeLong(packet.getUniqueId().getLeastSignificantBits());
        dataOutputStream.writeLong(packet.getUniqueId().getMostSignificantBits());
        dataOutputStream.writeInt(packet.getData().length);
        dataOutputStream.write(packet.getData());
    }
}
