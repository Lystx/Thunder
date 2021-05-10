package io.thunder.codec.impl;

import io.thunder.codec.PacketDecoder;
import io.thunder.connection.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.lang.reflect.Constructor;

public class DefaultPacketDecoder extends PacketDecoder {


    @Override
    public Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection) throws Exception {

        String s = buf.readString();
        try {
            Class<? extends Packet> _class = (Class<? extends Packet>) Class.forName(s);



            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            Packet bufferedPacket = null;
            for (Constructor<?> declaredConstructor : _class.getDeclaredConstructors()) {
                Object[] args = new Object[declaredConstructor.getParameters().length];
                for (int i = 0; i < declaredConstructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = declaredConstructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                bufferedPacket = (Packet) declaredConstructor.newInstance(args);
            }

            if (bufferedPacket == null) {
                bufferedPacket = _class.newInstance();
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
            //IGNORING NOT BUFFERED PACKET
        }

        return null;
    }
}
