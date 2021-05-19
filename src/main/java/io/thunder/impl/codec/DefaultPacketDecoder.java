package io.thunder.impl.codec;

import io.thunder.Thunder;
import io.thunder.connection.codec.PacketDecoder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.ErrorHandler;
import io.thunder.utils.ThunderUtils;

public class DefaultPacketDecoder extends PacketDecoder {


    @Override
    public Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection, String clazz) throws Exception {

        try {
            Class<? extends Packet> _class = (Class<? extends Packet>) Class.forName(clazz);

            Packet bufferedPacket = ThunderUtils.getInstance(_class);

            if (bufferedPacket == null) {
                bufferedPacket = Packet.newInstance();
            }

            //Setting values
            bufferedPacket.setProcessingTime(System.currentTimeMillis() - packet.getProcessingTime());
            bufferedPacket.setConnection(thunderConnection);
            bufferedPacket.setChannel(thunderConnection.getChannel());
            bufferedPacket.setUniqueId(packet.getUniqueId());
            bufferedPacket.setProtocolId(packet.getProtocolId());
            bufferedPacket.setProtocolVersion(packet.getProtocolVersion());

            //Reads the packet with the Reader
            bufferedPacket.read(buf);

            return bufferedPacket;
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                return null;
            }
            Thunder.ERROR_HANDLER.onPacketFailure(packet, packet.getClass().getName(), e);
            //IGNORING NOT BUFFERED PACKET
        }

        return null;
    }
}
