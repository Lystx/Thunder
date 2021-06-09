package io.thunder.packet.impl;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

/**
 * This class is used to send an Packet without having to buf every value
 */
public class JsonPacket extends EmptyPacket {

    public JsonPacket() {
        this.json = true;
    }

}
