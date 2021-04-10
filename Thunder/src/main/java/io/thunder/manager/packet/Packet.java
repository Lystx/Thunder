

package io.thunder.manager.packet;

import java.io.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * The main class to manage a Data-Switch
 * this contains all the data in a byte-array
 * and
 */
@Getter @RequiredArgsConstructor
public class Packet {

    private final short packetID;
    private final byte[] data;

    @Setter
    private long processingTime;

    private PacketReader packetReader;

    /**
     * Writes a packet to a Destination (Mostly another {@link io.thunder.connection.ThunderClient}
     * or {@link io.thunder.connection.ThunderServer} and sets the ProcessingTime of the packet to now
     *
     * @param dataOutputStream the {@link OutputStream} to write the Packet to
     * @throws IOException
     */
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong((processingTime = System.currentTimeMillis()));
        dataOutputStream.writeShort(packetID);
        dataOutputStream.writeInt(this.data.length);
        dataOutputStream.write(data);
    }

    /**
     * Returns the {@link PacketReader} for the current Packet.
     * If it's not set it will create a new PacketReader for the Packet
     *
     * @return PacketReader for this packet
     */
    public PacketReader reader() {
        if (packetReader == null) {
            packetReader = new PacketReader(this);
        }
        return packetReader;
    }

    @Override
    public String toString() {
        return "ID: [" + packetID + "] Data: [" + data.length + " bytes] Took : [" + processingTime + "ms]";
    }
}
