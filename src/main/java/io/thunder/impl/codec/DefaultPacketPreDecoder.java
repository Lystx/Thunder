package io.thunder.impl.codec;

import io.thunder.connection.codec.PacketPreDecoder;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.util.UUID;

public class DefaultPacketPreDecoder extends PacketPreDecoder {

    /**
     * Pre-Decodes a Packet from a Buffer
     *
     * @param buf the buffer
     * @return Packet
     * @throws Exception if something goes wrong
     */
    @Override
    public Packet decode(PacketBuffer buf) throws Exception {

        Packet packet = Packet.newInstance();

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
