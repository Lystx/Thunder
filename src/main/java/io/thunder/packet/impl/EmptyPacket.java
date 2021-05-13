package io.thunder.packet.impl;

import io.thunder.connection.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

public class EmptyPacket extends Packet {

    @Override
    public final void write(PacketBuffer buf) {

    }

    @Override
    public final void read(PacketBuffer buf) {

    }


    @Override
    public final void handle(ThunderConnection thunderConnection) {
        super.handle(thunderConnection);
    }
}
