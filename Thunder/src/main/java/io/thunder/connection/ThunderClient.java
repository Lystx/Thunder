

package io.thunder.connection;

import io.thunder.Thunder;
import io.thunder.manager.packet.BufferedPacket;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.ThunderPacket;
import io.thunder.manager.utils.PacketCompressor;
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
     * Connects to a {@link ThunderServer}
     * @param host the host to connect to
     * @param port the port connect to
     */
    void connect(String host, int port);

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
        if (Thunder.USE_COMPRESSOR) {
            PacketCompressor.compress(packet).write(dataOutputStream);
        } else {
            packet.write(dataOutputStream);
        }
    }

    /**
     * Returns Information on this Client
     * @return
     */
    default String asString() {
        return "[" + this.getSocket().getInetAddress().toString() + "@" + getUniqueId() + "]";
    }

    /**
     * Writes a Packet and transforms it
     * if its not a raw Packet
     * @param packet the packet to send
     */
    default void writePacket(ThunderPacket packet) {
        if (packet instanceof BufferedPacket) {
            this.writePacket(ThunderConnection.fromBuffered((BufferedPacket)packet));
        } else {
            this.writePacket(ThunderConnection.fromAbstract(packet));
        }
    }

}
