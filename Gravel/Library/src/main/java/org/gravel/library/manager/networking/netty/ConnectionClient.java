package org.gravel.library.manager.networking.netty;


import io.thunder.Thunder;
import io.thunder.connection.ThunderClient;
import io.thunder.connection.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.ThunderPacket;
import lombok.Getter;
import org.gravel.library.manager.networking.connection.packet.PacketState;

import java.io.IOException;
import java.util.function.Consumer;

@Getter
public class ConnectionClient extends GravelConnection {

    private final ThunderClient thunderClient;

    public ConnectionClient(String host, int port) {
        super(host, port);
        this.thunderClient = Thunder.createClient();
        this.thunderClient.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderClient thunderClient) {}

            @Override
            public void handleDisconnect(ThunderClient thunderClient) {}

            @Override
            public void handlePacket(ThunderPacket thunderPacket, ThunderClient thunderClient) throws IOException {
                packetAdapter.handelAdapterHandler(thunderPacket);
            }
        });
    }

    @Override
    public void run() {
        this.thunderClient.connect(host, port);
    }

    public void sendPacket(ThunderPacket packet) {
        this.thunderClient.sendPacket(packet);
    }

    @Override
    public void sendPacket(ThunderPacket paramPacket, Consumer<PacketState> paramConsumer) {
        this.thunderClient.sendPacket(paramPacket);
    }

}
