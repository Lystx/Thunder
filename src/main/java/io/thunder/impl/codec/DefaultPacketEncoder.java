package io.thunder.impl.codec;

import io.thunder.Thunder;
import io.thunder.connection.codec.PacketEncoder;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.utils.vson.annotation.other.Vson;
import io.thunder.utils.vson.enums.FileFormat;

import java.io.DataOutputStream;
import java.io.IOException;

public class DefaultPacketEncoder extends PacketEncoder {

    /**
     * Encodes a Packet
     * @param packet the packet to encode
     * @param dataOutputStream the output to transfer the data to
     * @param buf the buf to work with
     * @throws Exception if something goes wrong
     */
    @Override
    public void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception {

        buf.writeString(packet.getClass().getName());
        buf.writeString(packet.isJson() ? Vson.get().createTree(packet).toVson().toString(FileFormat.RAW_JSON) : "not_json");
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
            Thunder.ERROR_HANDLER.onPacketFailure(packet, packet.getClass().getName(), e);
        }

    }
}
