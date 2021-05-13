package io.thunder.impl.channel;


import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderSession;
import io.thunder.packet.Packet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientThunderChannel implements ThunderChannel {

    private final ThunderClient thunderClient;

    /**
     * Creates a new {@link ThunderChannel} for the {@link ThunderClient}
     *
     * @param thunderClient the Client
     * @return created Channel
     */
    public synchronized static ClientThunderChannel newInstance(ThunderClient thunderClient) {
        return new ClientThunderChannel(thunderClient);
    }

    /**
     * Sends a Packet to the channel
     *
     * @param packet the Packet to write
     */
    @Override @SneakyThrows
    public synchronized void processIn(Packet packet) {

        packet.setChannel(thunderClient.getChannel());
        packet.setConnection(this.thunderClient);

        ThunderConnection.processOut(packet, this.getOut());
    }

    /**
     * Sends a Packet from this Channel to all others
     *
     * @param packet the Packet to send
     */
    @Override @SneakyThrows
    public synchronized void processOut(Packet packet) {

        packet.setChannel(this.thunderClient.getChannel());
        packet.setConnection(this.thunderClient);

        DataOutputStream out = this.getOut();
        ThunderConnection.processOut(packet, out);
        out.flush();
    }

    /**
     * Sends a Packet to a specific {@link ThunderChannel}
     *
     * @param packet the Packet to send
     * @param thunderChannel the Channel to receive it
     */
    @Override
    public synchronized void processOut(Packet packet, ThunderChannel thunderChannel) {
        throw new UnsupportedOperationException("Not available for ThunderClient!");
    }

    /**
     * Flushes the Channel
     */
    @Override @SneakyThrows
    public synchronized void flush() {
        this.getOut().flush();
    }

    /**
     * Returns the OutputStream of this Channel
     *
     * @return nothing because Server
     */
    @Override @SneakyThrows
    public synchronized DataOutputStream getOut() {
        return new DataOutputStream(this.thunderClient.getSocket().getOutputStream());
    }

    /**
     * Returns the InputStream of this Channel
     *
     * @return nothing because Server
     */
    @Override @SneakyThrows
    public synchronized DataInputStream getIn() {
        return new DataInputStream(this.thunderClient.getSocket().getInputStream());
    }

    /**
     * Returns the remoteAddress of this channel
     *
     * @return socketAddress
     */
    @Override
    public synchronized SocketAddress remoteAddress() {
        return this.thunderClient.getSocket().getRemoteSocketAddress();
    }

    /**
     * Returns the localAddress of this channel
     *
     * @return socketAddress
     */
    @Override
    public synchronized SocketAddress localAddress() {
        return this.thunderClient.getSocket().getLocalSocketAddress();
    }

    /**
     * Returns the Session of this Channel
     *
     * @return session
     */
    @Override
    public synchronized ThunderSession getSession() {
        return this.thunderClient.getSession();
    }

    /**
     * Checks if the Channel is opened
     * @return boolean
     */
    @Override
    public synchronized boolean isOpen() {
        return this.thunderClient.getSocket().isConnected();
    }

    /**
     * Closes the Channel
     *
     * @throws IOException if something goes wrong
     */
    @Override
    public synchronized void close() throws IOException {
        this.thunderClient.getSocket().close();
    }

    /**
     * Checks if the connection is valid
     *
     * @return boolean
     */
    @Override
    public boolean isValid() {
        return this.thunderClient.getSocket() != null;
    }
}


