package io.thunder.packet.handler;

import io.thunder.packet.Packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This PacketAdapter manages all of the
 * {@link PacketHandler} interfaces and handles
 * them if a packet gets called and should be handled
 */
public class PacketAdapter {

    /**
     * The list of the registered {@link PacketHandler}s
     */
    private final List<PacketHandler> packetHandlers;

    /**
     * Creating the List of PacketHandler
     */
    public PacketAdapter() {
        this.packetHandlers = new LinkedList<>();
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
     * Handles the given {@link Packet}
     * and iterates through all registered {@link PacketHandler}s
     * and calls the {@link PacketHandler#handle(Packet)} Method
     *
     * @param packet the Packet to handle
     */
    public void handle(Packet packet) {
        for (PacketHandler packetHandler : new ArrayList<>(this.packetHandlers)) {
            packetHandler.handle(packet);
        }
    }

}
