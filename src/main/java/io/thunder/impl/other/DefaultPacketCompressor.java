package io.thunder.impl.other;

import io.thunder.connection.extra.PacketCompressor;
import io.thunder.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DefaultPacketCompressor implements PacketCompressor {

    @Override
    public Packet compress(Packet packet) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)
        {
            {
                def.setLevel(Deflater.BEST_COMPRESSION);
            }
        };

        gzipOutputStream.write(packet.getData());
        gzipOutputStream.close();

        Packet newPacket = Packet.newInstance();
        newPacket.setData(byteArrayOutputStream.toByteArray());

        return newPacket;
    }

    @Override
    public Packet decompress(Packet packet) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(packet.getData());
        GZIPInputStream in = new GZIPInputStream(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[16];
        int i;
        while ((i = in.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, i);
        }

        Packet newPacket = Packet.newInstance();
        newPacket.setData(outputStream.toByteArray());

        return newPacket;
    }
}
