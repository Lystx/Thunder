package io.thunder.impl.codec;

import io.thunder.Thunder;
import io.thunder.connection.codec.PacketDecoder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.ThunderUtils;

public class DefaultPacketDecoder extends PacketDecoder {


    /**
     * Decodes a Packet
     *
     * @param packet the packet to decode
     * @param buf the buf to work with
     * @param thunderConnection the connection it came from
     * @param clazz the name of the class
     * @return decoded Packet
     * @throws Exception if something goes wrong
     */
    @Override
    public Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection, String clazz, String data) throws Exception {

        try {
            Class<? extends Packet> _class = (Class<? extends Packet>) Class.forName(clazz);
            Packet bufferedPacket = null;
            if (data.equals("not_json")) {
                bufferedPacket = ThunderUtils.getInstance(_class);
            } else {
                try {
                    bufferedPacket = Thunder.JSON_INSTANCE.fromJson(data, (Class<Packet>) _class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bufferedPacket == null) {
                bufferedPacket = packet;
            }

            //Setting values
            bufferedPacket.setJson(packet.isJson());
            bufferedPacket.setProcessingTime(System.currentTimeMillis() - packet.getProcessingTime());
            bufferedPacket.setConnection(thunderConnection);
            bufferedPacket.setChannel(thunderConnection.getChannel());
            bufferedPacket.setUniqueId(packet.getUniqueId());
            bufferedPacket.setProtocolId(packet.getProtocolId());
            bufferedPacket.setProtocolVersion(packet.getProtocolVersion());

            //Reads the packet with the Reader

            if (!bufferedPacket.isJson()) {
                bufferedPacket.read(buf);
            }

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
