

package io.thunder.connection.base;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.impl.connection.ProvidedThunderClient;
import io.thunder.packet.Packet;
import io.thunder.utils.objects.ThunderAction;
import lombok.SneakyThrows;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This is the class for the {@link ThunderClient}
 * it extends from the {@link ThunderConnection}
 *
 * but has it own methods like connecting, setting the Socket
 * and so on.
 *
 * The Client has the special feature that you can write single Packets to
 * it directly and not only to all connected clients from a server
 */
public interface ThunderClient extends ThunderConnection {

    /**
     * Creates a new {@link ThunderClient}
     *
     * @return new instance of client
     */
    static ThunderClient newInstance() {
        return ProvidedThunderClient.newInstance();
    }

    /**
     * Connects to a {@link ThunderServer}
     * @param host the host to connect to
     * @param port the port connect to
     */
    ThunderAction<ThunderClient> connect(String host, int port);

    /**
     * Sets the socket of the Client
     * @param socket the socket
     * @throws IOException if something goes wrong
     */
    void setSocket(Socket socket) throws IOException;

    /**
     * Returns the {@link Socket} of the Client
     * @return Socket
     */
    Socket getSocket();

    /**
     * Writes a packet directly into the
     * {@link java.io.OutputStream} of the current Socket
     *
     * @param packet packet to write
     */
    @SneakyThrows
    default void writePacket(Packet packet) {
        DataOutputStream dataOutputStream = new DataOutputStream(this.getSocket().getOutputStream());

        ThunderConnection.processOut(packet, dataOutputStream);
    }

}
