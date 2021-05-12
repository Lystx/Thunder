package io.thunder.impl.channel;


import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.packet.Packet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerThunderChannel implements ThunderChannel {

    private final ThunderServer thunderServer;

    public static ServerThunderChannel newInstance(ThunderServer thunderServer) {
        return new ServerThunderChannel(thunderServer);
    }

    @Override
    public void processIn(Packet packet) {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }

    @Override @SneakyThrows
    public void processOut(Packet packet) {

        packet.setChannel(this.thunderServer.getChannel());
        packet.setConnection(this.thunderServer);

        for (ThunderClient client : this.thunderServer.getClients()) {
            ThunderConnection.processOut(
                    packet,
                    new DataOutputStream(
                            client
                                    .getSocket()
                                    .getOutputStream()
                    )
            );
        }
    }

    @Override @SneakyThrows
    public void processOut(Packet packet, ThunderChannel thunderChannel) {

        packet.setChannel(this.thunderServer.getChannel());
        packet.setConnection(this.thunderServer);

        DataOutputStream dataOutputStream = thunderChannel.getOut();
        ThunderConnection.processOut(packet, dataOutputStream);
    }

    @Override @SneakyThrows
    public void flush() {
        for (ThunderClient client : this.thunderServer.getClients()) {
            client.getSocket().getOutputStream().flush();
        }
    }

    @Override
    public DataOutputStream getOut() {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }

    @Override
    public DataInputStream getIn() {
        throw new UnsupportedOperationException("Not available for ThunderServer!");
    }

    @Override
    public SocketAddress remoteAddress() {
        return new InetSocketAddress(this.thunderServer.getServer().getInetAddress().getHostName(), this.thunderServer.getServer().getLocalPort());
    }

    @Override
    public SocketAddress localAddress() {
        return this.thunderServer.getServer().getLocalSocketAddress();
    }

    @Override
    public ThunderSession getSession() {
        return this.thunderServer.getSession();
    }

    @Override
    public boolean isOpen() {
        return !this.thunderServer.getServer().isClosed();
    }

    @Override
    public void close() throws IOException {
        for (ThunderClient client : this.thunderServer.getClients()) {
            client.disconnect();
        }
        this.thunderServer.getServer().close();
    }
}