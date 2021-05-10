package io.thunder.codec.impl;

import io.thunder.Thunder;
import io.thunder.codec.PacketEncoder;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.io.DataOutputStream;
import java.io.IOException;

public class DefaultPacketEncoder extends PacketEncoder {

    @Override
    public void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception {

        buf.writeString(packet.getClass().getName());
        packet.write(buf);

        try {
            buf.getDataOutputStream().close();

            packet.setData(buf.getByteArrayOutputStream().toByteArray());

            dataOutputStream.writeInt(packet.getProtocolId());
            dataOutputStream.writeInt(packet.getProtocolVersion());
            dataOutputStream.writeLong(packet.getUniqueId().getLeastSignificantBits());
            dataOutputStream.writeLong(packet.getUniqueId().getMostSignificantBits());
            dataOutputStream.writeInt(packet.getData().length);
            dataOutputStream.write(packet.getData());
            dataOutputStream.writeLong(System.currentTimeMillis());
        } catch (IOException e) {
            Thunder.LOGGER.log(LogLevel.ERROR, "Packet couldn't be built (" + getClass().getSimpleName() + ")");
        }

    }
}
