

package io.thunder.connection.extra;

import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.featured.QueryPacket;
import io.vson.elements.object.VsonObject;
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
     * Server:
     *    -> Called when a Client connects to the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when connected to the server
     *     @param thunderConnection > The server
     */
    void handleConnect(ThunderConnection thunderConnection);

    /**
     * Server:
     *    -> Called when a Client disconnects from the server
     *     @param thunderConnection > The client
     * Client:
     *    -> Called when disconnected from the server
     *     @param thunderConnection > The server
     */
    void handleDisconnect(ThunderConnection thunderConnection);

    /**
     * Called when a Packet gets handled
     * This packet can either be a raw {@link ThunderPacket}
     * or a {@link BufferedPacket} but it can't be a {@link QueryPacket} !
     *
     * @param packet the received Packet
     * @param thunderConnection the connection it came from (Server or Client)
     * @throws IOException if something went wrong
     */
    void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException;

    /**
     * Raw Method before the Packet gets handle to hook yourself in
     *
     * @param packet the raw packet with all its data
     * @param thunderConnection the connection it came from
     * @throws IOException if something went wrong
     */
    void read(Packet packet, ThunderConnection thunderConnection) throws IOException;


    /**
     * Handles a {@link BufferedPacket}
     *
     * @param packet the packet
     * @param thunderConnection the connection it came from
     * @param _class the class of the packet
     * @throws Exception if something went wrong
     */
    default void handleBuffered(Packet packet, ThunderConnection thunderConnection, Class<? extends BufferedPacket> _class) throws Exception {
        BufferedPacket bufferedPacket = null;

        //Iterates through all Constructors to create a new Instance of the Object
        //And to set all values to null, -1 or false
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
            bufferedPacket = (BufferedPacket) declaredConstructor.newInstance(args);
        }

        if (bufferedPacket == null) {
            bufferedPacket = _class.newInstance();
        }

        //Reads the packet with the Reader
        bufferedPacket.read(packet.reader());
        bufferedPacket.setProcessingTime(System.currentTimeMillis() - packet.getProcessingTime());
        packet.setProcessingTime(bufferedPacket.getProcessingTime());
        packet.setPacket(_class.getSimpleName());
        bufferedPacket.setData(packet.toString());

        bufferedPacket.handle(thunderConnection);
        this.handlePacket(bufferedPacket, thunderConnection); //Then handling the ready built packet
    }

    /**
     * Handles a raw packet intern
     * @param packet the packet to handle
     * @param thunderConnection the connection it came from
     * @throws IOException if something went wrong
     */
    @SneakyThrows
    default void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
        this.read(packet, thunderConnection);

        String s = packet.reader().readString();
        try {
            Class<? extends BufferedPacket> _class = (Class<? extends BufferedPacket>) Class.forName(s);
            this.handleBuffered(packet, thunderConnection, _class);
            return;
        } catch (ClassNotFoundException e) {
            //IGNORING NOT BUFFERED PACKET
        }

        try {
            if (s == null) {
                return;
            }
            VsonObject vsonObject = new VsonObject(s);
            Class<?> _class = Class.forName(vsonObject.get("_class").asString());
            ThunderPacket abstractPacket = (ThunderPacket) vsonObject.getObject("_abstractPacket", _class);
            abstractPacket.setProcessingTime(System.currentTimeMillis() - vsonObject.get("_processingTime").asLong());
            packet.setProcessingTime(abstractPacket.getProcessingTime());
            packet.setPacket(_class.getSimpleName());
            abstractPacket.setData(packet.toString());

            abstractPacket.handle(thunderConnection);
            this.handlePacket(abstractPacket, thunderConnection);
            thunderConnection.getPacketAdapter().handle(abstractPacket);

        } catch (Exception ex) {
            ex.printStackTrace();
            //Received Data is not a JsonObject!
        }
    }



}
