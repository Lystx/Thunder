package io.thunder.manager.packet;


import io.thunder.connection.ThunderConnection;
import lombok.Getter;
import lombok.Setter;


/**
 * This class should not be used as a raw
 * because it takes much time to build the
 * whole Packet into a JsonObject and then parse
 * it back to the given packet (this takes about 60-100ms)
 *
 * but when using the {@link BufferedPacket} you can
 * serialize the Packet yourself and this takes
 * only about 3 - 10 ms
 */
 @Setter @Deprecated
public abstract class ThunderPacket {

    @Getter
    private long processingTime; //The time the packet took to process
    private String data; //The data of the packet

    @Override
    public String toString() {
        return this.data;
    }

    /**
     * Will be called when the packet is received and fully
     * constructed. Either {@link ThunderPacket} or {@link BufferedPacket}
     *
     * @param thunderConnection the connection who receives
     */
    public void handle(ThunderConnection thunderConnection) {
    }

}
