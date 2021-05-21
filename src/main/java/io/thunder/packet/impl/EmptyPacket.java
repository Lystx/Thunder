package io.thunder.packet.impl;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

/**
 * This class is used to send an EmptyPacket
 * with no data in it
 *
 * For example you can create a "PacketShutdown" which extends {@link EmptyPacket}
 * and has no values and just shuts down some process
 */
public class EmptyPacket extends Packet {

    @Override
    public final void write(PacketBuffer buf) {}

    @Override
    public final void read(PacketBuffer buf) {}


}
