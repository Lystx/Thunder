package io.thunder.connection.data;

import io.thunder.Thunder;
import io.thunder.connection.codec.PacketCodec;
import io.thunder.connection.codec.PacketDecoder;
import io.thunder.connection.codec.PacketEncoder;
import io.thunder.connection.codec.PacketPreDecoder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.PacketCompressor;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.base.ThunderSession;
import io.thunder.impl.other.ProvidedPacketBuffer;
import io.thunder.packet.*;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.object.ObjectHandler;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.Response;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.logger.LogLevel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * This interface is used to define the {@link ThunderServer}
 * and the {@link ThunderClient}
 *
 * You can also use a ThunderConnection as variable and be able
 * to insert both {@link ThunderClient} and {@link ThunderServer}
 * because they extend from this interface
 */
public interface ThunderConnection {

    /**
     * Adds a {@link ThunderListener} to the current
     * Connection
     *
     * @param serverListener the Listener to add
     */
    void addSessionListener(ThunderListener serverListener);

    /**
     * Adds a {@link ObjectHandler} to the current
     * Connection
     *
     * @param objectHandler the handler to add
     */
    void addObjectListener(ObjectHandler<?> objectHandler);

    /**
     * Adds a {@link PacketCompressor} to the current
     * Connection to (de-)compress all in- or outcoming packets
     *
     * @param compressor the compressor to add
     */
    void addCompressor(PacketCompressor compressor);


    List<PacketCompressor> getPacketCompressors();

    /**
     * Returns all the registered {@link ObjectHandler}s
     *
     * @return list of handlers
     */
    List<ObjectHandler<?>> getObjectHandlers();

    /**
     * Sets the name of this {@link ThunderConnection}
     * for identification later
     *
     * @param name the name of the conneciton
     */
    void setName(String name);

    /**
     * Sends a packet from the current connection
     * to the (server / client)
     * @param packet the packet to send
     */
    void sendPacket(Packet packet);

    /**
     * Sends an Object to the other {@link ThunderConnection}s
     *
     * @param object the objects
     */
    void sendObject(Object object);

    default ThunderConnection addPacketHandler(PacketHandler packetHandler) {
        this.getPacketAdapter().addHandler(packetHandler);
        return this;
    }

    /**
     * Calls the {@link ThunderConnection#transferToResponse(Packet)} Method
     * It just simplify's it into a {@link Consumer} to work with it
     *
     * @param packet the Packet to send
     * @param consumer the Consumer to work with
     */
    default void sendPacket(Packet packet, Consumer<Response> consumer) {
        this.sendPacket(packet, consumer, 3000);
    }

    default void sendPacket(Packet packet, Consumer<Response> consumer, int timeOut) {
        Thunder.EXECUTOR_SERVICE.execute(() -> consumer.accept(this.transferToResponse(packet, timeOut)));
    }

    default Response transferToResponse(Packet pp) {
        return this.transferToResponse(pp, 3000);
    }
    /**
     * Calls the {@link ThunderConnection#sendPacket(Packet)} Method
     * This will wait for the given Packet to respond
     * And if the {@link Packet} responded it will return its {@link Response}
     * to work with it
     *
     * @param pp the Packet await Response for
     * @return the Response the Packet gets
     */
    default Response transferToResponse(Packet pp, int timeOut) {
        Response[] response = {null};
        getPacketAdapter().addHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                Thunder.LOGGER.log(LogLevel.DEBUG, "Response with UUID " + pp.getUniqueId() + " has received Packet " + packet.getClass().getSimpleName() + " with UUID " + packet.getUniqueId() + "!");
                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getUniqueId().equals(pp.getUniqueId())) {
                        response[0] = new Response(packetRespond);
                        getPacketAdapter().removeHandler(this);
                        Thunder.LOGGER.log(LogLevel.DEBUG, "Response with UUID " + pp.getUniqueId() + " has set Response and removed Handler!");
                    }
                }
            }
        });

        this.sendPacket(pp);
        Thunder.LOGGER.log(LogLevel.DEBUG, "Response with UUID " + pp.getUniqueId() + " has sent Packet!");

        int count = 0;
        while (response[0] == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            Thunder.LOGGER.log(LogLevel.ERROR, "Response with UUID " + pp.getUniqueId() + " has timed out :(");
            response[0] = new Response(new PacketRespond("The Request timed out", ResponseStatus.FAILED));
        }
        return response[0];
    }

    /**
     * @return the PacketAdapter
     */
    PacketAdapter getPacketAdapter();

    /**
     * Flushes the Connection
     * and clears every Packet thats still
     * in the ConnectionGateway
     */
    void flush();

    /**
     * Disconnects the connection
     * either from the server or
     * to to the server
     */
    void disconnect();

    /**
     * Every connection has it's own Session
     * to identify it. This can be useful for Developers
     * to check Sessions and so on.
     *
     * @return the Session of this Connection
     */
    ThunderSession getSession();

    /**
     * Adds a De or Encoder to this Connection
     *
     * @param packetCodec the PacketCodec
     */
    void addCodec(PacketCodec packetCodec);

    /**
     * Returns the Channel of the Connection
     * where all the data is send
     *
     * @return channel of connection
     */
    ThunderChannel getChannel();

    /**
     * Returns the Encoder
     *
     * @return encoder
     */
    PacketEncoder getEncoder();

    /**
     * Returns the PreDecoder
     *
     * @return pre decdoer
     */
    PacketPreDecoder getPreDecoder();

    /**
     * Returns the Decoder
     *
     * @return decoder
     */
    PacketDecoder getDecoder();


    /**
     * Checks if the Connection is still stable
     * @return boolean if connected
     */
    boolean isConnected();


    /**
     * Writes a packet to a Destination (Mostly another {@link ThunderClient}
     * or {@link ThunderServer} and sets the ProcessingTime of the packet to now
     *
     * @param dataOutputStream the {@link OutputStream} to write the Packet to
     * @throws IOException
     */
    static void processOut(Packet packet, DataOutputStream dataOutputStream) throws IOException {

        ThunderConnection connection = packet.getConnection();

        if (connection.getPacketCompressors().isEmpty()) {
            for (PacketCompressor packetCompressor : connection.getPacketCompressors()) {
                try {
                    packet = packetCompressor.decompress(packet);
                } catch (Exception e) {
                    Thunder.ERROR_HANDLER.onPacketFailure(packet, packet.getClass().getName(), e);
                }
            }
        }


        PacketEncoder encoder = packet.getConnection().getEncoder();
        try {
            encoder.encode(packet, dataOutputStream, ProvidedPacketBuffer.newInstance());
        } catch (Exception e) {
            Thunder.ERROR_HANDLER.onPacketFailure(packet, packet.getClass().getName(), e);
        }

    }
}
