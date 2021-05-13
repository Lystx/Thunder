package io.thunder.impl.codec;

import io.thunder.connection.codec.PacketDecoder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DefaultPacketDecoder extends PacketDecoder {


    @Override
    public Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection) throws Exception {

        String s = buf.readString();
        try {
            Class<? extends Packet> _class = (Class<? extends Packet>) Class.forName(s);

            Constructor<?> constructor;

            try {
                List<Constructor<?>> constructors = Arrays.asList(_class.getDeclaredConstructors());

                constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                constructor = constructors.get(constructors.size() - 1);
            } catch (Exception e) {
                constructor = null;
            }


            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            Packet bufferedPacket = null;
            if (constructor != null) {
                Object[] args = new Object[constructor.getParameters().length];
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                        args[i] = -1;
                    } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                bufferedPacket = (Packet) constructor.newInstance(args);
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
