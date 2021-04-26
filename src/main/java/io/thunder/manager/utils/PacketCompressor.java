package io.thunder.manager.utils;

import io.thunder.manager.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PacketCompressor {

    /**
     * Decompresses given Packet
     * @param packet Compressed Packet
     * @return Decompressed Packet
     * @throws IOException when unable to decompress
     */
    public static Packet decompress(Packet packet) throws IOException {

        long time = packet.getProcessingTime();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[16];
        int bytesInflated;
        while ((bytesInflated = gzipInputStream.read(buffer)) >= 0) {
            byteArrayOutputStream.write(buffer, 0, bytesInflated);
        }

        final Packet packet1 = new Packet(byteArrayOutputStream.toByteArray());
        packet1.setProcessingTime(time);
        return packet1;
    }

    /**
     * Compresses given Packet. Note that this can increase the total size when used incorrectly
     * @param packet Packet to compress
     * @return Compressed Packet
     * @throws IOException when unable to compress
     */
    public static Packet compress(Packet packet) throws IOException {
        long time = packet.getProcessingTime();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream) {
            {
                def.setLevel(Deflater.BEST_COMPRESSION);
            }
        };

        gzipOutputStream.write(packet.getData());
        gzipOutputStream.close();
        final Packet packet1 = new Packet(byteArrayOutputStream.toByteArray());
        packet1.setProcessingTime(time);
        return packet1;
    }
}
