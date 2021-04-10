package io.lightning.network.packet;

import java.util.LinkedList;
import java.util.List;

public class PacketAdapter {

    private final List<PacketHandler> packetHandlers;

    public PacketAdapter() {
        this.packetHandlers = new LinkedList<>();
    }

    public void registerHandler(PacketHandler packetHandler) {
        this.packetHandlers.add(packetHandler);
    }

    public void unregisterHandler(PacketHandler packetHandler) {
        this.packetHandlers.remove(packetHandler);
    }

    public <T extends Packet> void handle(Packet packet) {
        for (PacketHandler packetHandler : this.packetHandlers) {
            packetHandler.handle(packet);
        }
    }
}
