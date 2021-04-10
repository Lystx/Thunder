package org.gravel.library.manager.networking.netty;

import io.thunder.manager.packet.ThunderPacket;
import lombok.Getter;
import org.gravel.library.manager.networking.connection.adapter.PacketAdapter;
import org.gravel.library.manager.networking.connection.packet.PacketState;

import java.util.function.Consumer;

@Getter
public abstract class GravelConnection extends Thread {

    protected boolean running;
    protected final String host;
    protected final int port;
    protected final PacketAdapter packetAdapter;

    public GravelConnection(String host, int port) {
        this.packetAdapter = new PacketAdapter();
        this.running = true;
        this.host = host;
        this.port = port;
    }

    public abstract void sendPacket(ThunderPacket paramPacket);

    public abstract void sendPacket(ThunderPacket paramPacket, Consumer<PacketState> paramConsumer);

}
