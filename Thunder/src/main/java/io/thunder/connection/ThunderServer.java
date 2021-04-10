
package io.thunder.connection;

import io.thunder.manager.packet.Packet;

import java.net.ServerSocket;

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
     * Starts the Server on a given port
     * Host is your ip (makes sense doesn't it?)
     *
     * @param port the port the server should be running on
     */
    void start(int port);

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
     * Returns Information on the Server
     * @return
     */
    default String asString() {
        return "[" + this.getServer().getInetAddress().toString() + "@" + getUniqueId() + "]";
    }
}
