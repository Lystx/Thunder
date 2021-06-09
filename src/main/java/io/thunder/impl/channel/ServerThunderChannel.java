package io.thunder.impl.channel;


import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.base.ThunderSession;
import io.thunder.packet.Packet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerThunderChannel implements ThunderChannel {

    /**
     * The Server the channel belongs to
     */
    private final ThunderServer thunderServer;

    /**
     * Creates a new {@link ThunderChannel} for the {@link ThunderServer}
     *
     * @param thunderServer the server
     * @return created Channel
     */
    public static ServerThunderChannel newInstance(ThunderServer thunderServer) {
        return new ServerThunderChannel(thunderServer);
    }

    /**
     * Sends a Packet to the channel
     *
     * @param packet the Packet to write
     */
    @Override
    public void processIn(Packet packet) {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }


    /**
     * Sends a Packet from this Channel to all others
     *
     * @param packet the Packet to send
     */
    @Override @SneakyThrows
    public void processOut(Packet packet) {

        packet.setChannel(this.thunderServer.getChannel());
        packet.setConnection(this.thunderServer);

        for (ThunderClient client : new LinkedList<>(this.thunderServer.getClients())) {
            try {

                ThunderConnection.processOut(
                        packet,
                        new DataOutputStream(
                                client
                                        .getSocket()
                                        .getOutputStream()
                        )
                );
            } catch (Exception e) {
                //Server already closed ignoring
            }
        }
    }

    /**
     * Sends a Packet to a specific {@link ThunderChannel}
     *
     * @param packet the Packet to send
     * @param thunderChannel the Channel to receive it
     */
    @Override @SneakyThrows
    public void processOut(Packet packet, ThunderChannel thunderChannel) {

        packet.setChannel(this.thunderServer.getChannel());
        packet.setConnection(this.thunderServer);

        DataOutputStream dataOutputStream = thunderChannel.getOut();
        ThunderConnection.processOut(packet, dataOutputStream);
    }

    /**
     * Flushes the Channel
     */
    @Override @SneakyThrows
    public void flush() {
        for (ThunderClient client : this.thunderServer.getClients()) {
            client.getSocket().getOutputStream().flush();
        }
    }

    /**
     * Returns the OutputStream of this Channel
     *
     * @return nothing because Server
     */
    @Override
    public DataOutputStream getOut() {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }

    /**
     * Returns the InputStream of this Channel
     *
     * @return nothing because Server
     */
    @Override
    public DataInputStream getIn() {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }

    /**
     * Returns the remoteAddress of this channel
     *
     * @return socketAddress
     */
    @Override
    public SocketAddress remoteAddress() {
        return new InetSocketAddress(this.thunderServer.getServer().getInetAddress().getHostName(), this.thunderServer.getServer().getLocalPort());
    }

    /**
     * Returns the localAddress of this channel
     *
     * @return socketAddress
     */
    @Override
    public SocketAddress localAddress() {
        return this.thunderServer.getServer().getLocalSocketAddress();
    }

    /**
     * Returns the Session of this Channel
     *
     * @return session
     */
    @Override
    public ThunderSession getSession() {
        return this.thunderServer.getSession();
    }

    /**
     * Checks if the Channel is opened
     * @return boolean
     */
    @Override
    public boolean isOpen() {
        return this.thunderServer.getServer().isBound() && !thunderServer.getServer().isClosed();
    }

    /**
     * Closes the Channel
     *
     * @throws IOException if something goes wrong
     */
    @Override
    public void close() throws IOException {
        for (ThunderClient client : this.thunderServer.getClients()) {
            client.disconnect();
        }
        this.thunderServer.getServer().close();
    }

    /**
     * Checks if the connection is valid
     *
     * @return boolean
     */
    @Override
    public boolean isValid() {
        return this.thunderServer.getServer() != null;
    }
}