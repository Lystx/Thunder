package org.gravel.library.manager.networking.netty;

import io.thunder.Thunder;
import io.thunder.connection.ThunderClient;
import io.thunder.connection.ThunderListener;
import io.thunder.connection.ThunderServer;
import io.thunder.manager.packet.ThunderPacket;
import org.gravel.library.manager.networking.connection.packet.PacketState;

import java.io.IOException;
import java.util.function.Consumer;

public class ConnectionServer extends GravelConnection {

    private final ThunderServer thunderServer;

    public ConnectionServer(String host, int port) {
        super(host, port);

        this.thunderServer = Thunder.createServer();
        this.thunderServer.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderClient thunderClient) {
            }

            @Override
            public void handleDisconnect(ThunderClient thunderClient) {

            }

            @Override
            public void handlePacket(ThunderPacket thunderPacket, ThunderClient thunderClient) throws IOException {
                packetAdapter.handelAdapterHandler(thunderPacket);
            }
        });
    }

    @Override
    public void run() {
        this.thunderServer.start(this.port);
    }

    @Override
    public void sendPacket(ThunderPacket paramPacket) {
        this.thunderServer.sendPacket(paramPacket);
    }

    @Override
    public void sendPacket(ThunderPacket paramPacket, Consumer<PacketState> paramConsumer) {
        this.thunderServer.sendPacket(paramPacket);
    }
}
