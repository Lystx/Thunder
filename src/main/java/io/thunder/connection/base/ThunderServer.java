
package io.thunder.connection.base;

import io.thunder.Thunder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.impl.connection.ProvidedThunderServer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.Response;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.objects.ThunderAction;

import java.net.ServerSocket;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is the class for the {@link ThunderServer}
 * it extends from the {@link ThunderConnection}
 *
 * but has it own methods like starting, getting the Socket
 * and so on.
 *
 * The Server has the special feature that you can send a single
 * Packet to a specific {@link ThunderClient}
 */
public interface ThunderServer extends ThunderConnection {

    /**
     * Creates a new {@link ThunderServer}
     *
     * @return new instance of server
     */
    static ThunderServer newInstance() {
        return ProvidedThunderServer.newInstance();
    }

    /**
     * Lists all connected {@link ThunderClient}s
     *
     * @return list of clients
     */
    List<ThunderClient> getClients();

    /**
     * Starts the Server on a given port
     * Host is your ip (makes sense doesn't it?)
     *
     * @param port the port the server should be running on
     */
    ThunderAction<ThunderServer> start(int port);

    /**
     * Sends a {@link Packet} to a specific {@link ThunderSession}
     *
     * @param packet the Packet to send
     * @param session the Session to receive it
     */
    void sendPacket(Packet packet, ThunderSession session);

    /**
     * Sends a {@link Packet} to a specific {@link ThunderClient}
     *
     * @param packet the Packet to send
     * @param client the Client to receive it
     */
    void sendPacket(Packet packet, ThunderClient client);

    /**
     * @return the ServerSocket
     */
    ServerSocket getServer();


    /**
     * Calls the {@link ThunderConnection#transferToResponse(Packet)} Method
     * It just simplify's it into a {@link Consumer} to work with it
     *
     * @param packet the Packet to send
     * @param consumer the Consumer to work with
     */
    default void sendPacket(Packet packet, ThunderSession session, Consumer<Response> consumer) {
        Thunder.EXECUTOR_SERVICE.execute(() -> consumer.accept(this.transferToResponse(packet, session)));
    }


    /**
     * Calls the {@link ThunderConnection#sendPacket(Packet)} Method
     * This will wait for the given Packet to respond
     * And if the {@link Packet} responded it will return its {@link Response}
     * to work with it
     *
     * @param packet the Packet await Response for
     * @return the Response the Packet gets
     */
    default Response transferToResponse(Packet packet, ThunderSession session) {
        Response[] response = {null};
        ((ThunderServer) addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getUniqueId().equals(packetRespond.getUniqueId())) {
                        response[0] = new Response(packetRespond);
                        getPacketAdapter().removeHandler(this);
                    }
                }
            }
        })).sendPacket(packet, session);

        int count = 0;
        while (response[0] == null && count++ < 3000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= 2999) {
            response[0] = new Response(new PacketRespond("The Request timed out", ResponseStatus.FAILED));
        }
        return response[0];
    }
}
