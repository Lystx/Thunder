package io.thunder.connection.extra;

import io.thunder.packet.Packet;

public interface PacketCompressor {


    /**
     * Compresses the given {@link Packet} to another Packet
     *
     * @param packet the packet to compress
     * @return compressed Packet
     * @throws Exception if something goes wrong
     */
    Packet compress(Packet packet) throws Exception;

    /**
     * Decompresses the given {@link Packet} to another Packet
     *
     * @param packet the packet to decompress
     * @return decompressed Packet
     * @throws Exception if something goes wrong
     */
    Packet decompress(Packet packet) throws Exception;

}
