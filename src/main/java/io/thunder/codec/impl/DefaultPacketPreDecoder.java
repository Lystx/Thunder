package io.thunder.codec.impl;

import io.thunder.codec.PacketPreDecoder;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.util.UUID;

public class DefaultPacketPreDecoder extends PacketPreDecoder {

    @Override
    public Packet decode(PacketBuffer buf) throws Exception {

        Packet packet = new Packet() {public void write(PacketBuffer buf) {} public void read(PacketBuffer buf) {}};

        int protocolId = buf.readInt();
        int protocolVersion = buf.readInt();
        UUID uniqueId = new UUID(buf.readLong(), buf.readLong());
        int dataLength = buf.readInt();
        byte[] data = new byte[dataLength];
        buf.readFully(data);
        long time = buf.readLong();

        packet.setData(data);

        packet.setProcessingTime(time);
        packet.setProtocolId(protocolId);
        packet.setProtocolVersion(protocolVersion);
        packet.setUniqueId(uniqueId);

        return packet;
    }
}
