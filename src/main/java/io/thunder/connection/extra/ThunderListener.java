

package io.thunder.connection.extra;

import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.response.PacketRespond;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * This class is used to handle specific
 * actions from both connection types
 * that means the {@link ThunderListener} is suitable
 * for both {@link ThunderClient} and {@link ThunderServer}
 * only the {@link ThunderConnection} will be something different from
 * one of the types
 */
public interface ThunderListener {


    /**
     * Called when a Packet gets handled
     * This packet can be a raw {@link Packet}
     *
     * @param packet the received Packet
     * @param thunderConnection the connection it came from (Server or Client)
     * @throws IOException if something went wrong
     */
    void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException;


    /**
     * Server:
     *    -> Called when a Client connects to the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when connected to the server
     *     @param thunderConnection > The Client itsself
     */
    void handleConnect(ThunderConnection thunderConnection);

    /**
     * Server:
     *    -> Called when a Client disconnects from the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when disconnected from the server
     *     @param thunderConnection > The Client
     */
    void handleDisconnect(ThunderConnection thunderConnection);

    /**
     * Handles a raw packet intern
     * @param packet the packet to handle
     * @param thunderConnection the connection it came from
     * @throws IOException if something went wrong
     */
    @SneakyThrows
    default void readPacket(Packet packet, ThunderConnection thunderConnection) throws IOException {

        PacketBuffer buf = new PacketBuffer(packet);

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
            bufferedPacket.setUniqueId(packet.getUniqueId());
            bufferedPacket.setProtocolId(packet.getProtocolId());
            bufferedPacket.setProtocolVersion(packet.getProtocolVersion());

            //Reads the packet with the Reader
            bufferedPacket.read(buf);
            bufferedPacket.handle(thunderConnection);

            thunderConnection.getPacketAdapter().handle(bufferedPacket);
            if (packet instanceof PacketRespond) {
                return;
            }
            this.handlePacket(bufferedPacket, thunderConnection); //Then handling the ready built packet
        } catch (Exception e) {
            //IGNORING NOT BUFFERED PACKET
        }

    }



}
