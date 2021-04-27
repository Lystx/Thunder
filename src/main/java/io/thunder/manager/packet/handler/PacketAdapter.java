package io.thunder.manager.packet.handler;

import io.thunder.connection.ThunderConnection;
import io.thunder.connection.handler.ThunderPacketHandlerQuery;
import io.thunder.manager.packet.ThunderPacket;

import java.util.LinkedList;
import java.util.List;

/**
 * This PacketAdapter manages all of the
 * {@link PacketHandler} interfaces and handles
 * them if a packet gets called and should be handled
 */
public class PacketAdapter {

    private final List<PacketHandler> packetHandlers;

    /**
     * Creating the List of PacketHandler
     */
    public PacketAdapter(ThunderConnection thunderConnection) {
        this.packetHandlers = new LinkedList<>();
        this.packetHandlers.add(new ThunderPacketHandlerQuery(thunderConnection));
    }

    /**
     * Adds a {@link PacketHandler} to the list
     * @param packetHandler the PacketHandler to add
     */
    public void addHandler(PacketHandler packetHandler) {
        this.packetHandlers.add(packetHandler);
    }

    /**
     * Removes a {@link PacketHandler} from the list
     * @param packetHandler the PacketHandler to remove
     */
    public void removeHandler(PacketHandler packetHandler) {
        this.packetHandlers.remove(packetHandler);
    }

    /**
     * Handles the given {@link ThunderPacket}
     * and iterates through all registered {@link PacketHandler}s
     * and calls the {@link PacketHandler#handle(ThunderPacket)} Method
     *
     * @param packet the Packet to handle
     */
    public void handle(ThunderPacket packet) {
        for (PacketHandler packetHandler : this.packetHandlers) {
            packetHandler.handle(packet);
        }
    }

}
