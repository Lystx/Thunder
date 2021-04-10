package io.thunder.manager.packet;


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
@Getter @Setter
public abstract class ThunderPacket {

    private long processingTime; //The time the packet took to process
    private String data; //The data of the packet

    public abstract int getPacketID(); //The ID of the packet to identify it later

    @Override
    public String toString() {
        return this.data;
    }

}
