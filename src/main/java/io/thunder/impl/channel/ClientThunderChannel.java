package io.thunder.impl.channel;


import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.extra.ThunderSession;
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

    public static ClientThunderChannel newInstance(ThunderClient thunderClient) {
        return new ClientThunderChannel(thunderClient);
    }

    @Override @SneakyThrows
    public void processIn(Packet packet) {

        packet.setChannel(thunderClient.getChannel());
        packet.setConnection(this.thunderClient);

        ThunderConnection.processOut(packet, this.getOut());
    }

    @Override @SneakyThrows
    public void processOut(Packet packet) {

        packet.setChannel(this.thunderClient.getChannel());
        packet.setConnection(this.thunderClient);

        DataOutputStream out = this.getOut();
        ThunderConnection.processOut(packet, out);
        out.flush();
    }

    @Override
    public void processOut(Packet packet, ThunderChannel thunderChannel) {
        throw new UnsupportedOperationException("Not available for ThunderClient!");
    }

    @Override @SneakyThrows
    public void flush() {
        this.getOut().flush();
    }

    @Override @SneakyThrows
    public DataOutputStream getOut() {
        return new DataOutputStream(this.thunderClient.getSocket().getOutputStream());
    }

    @Override @SneakyThrows
    public DataInputStream getIn() {
        return new DataInputStream(this.thunderClient.getSocket().getInputStream());
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.thunderClient.getSocket().getRemoteSocketAddress();
    }

    @Override
    public SocketAddress localAddress() {
        return this.thunderClient.getSocket().getLocalSocketAddress();
    }

    @Override
    public ThunderSession getSession() {
        return this.thunderClient.getSession();
    }

    @Override
    public boolean isOpen() {
        return this.thunderClient.isConnected();
    }

    @Override
    public void close() throws IOException {
        this.thunderClient.getSocket().close();
    }
}


