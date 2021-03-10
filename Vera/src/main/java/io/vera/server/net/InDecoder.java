
package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.vera.logger.Logger;
import io.vera.server.VeraServer;
import io.vera.server.packet.Packet;
import io.vera.server.packet.PacketIn;
import io.vera.server.packet.PacketRegistry;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.Inflater;

import static io.vera.server.net.NetData.arr;


@ThreadSafe
public class InDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = Logger.get(InDecoder.class);

    private final Inflater inflater = new Inflater();

    private ByteBuf lastDecrypted;
    private NetClient client;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.client = NetClient.get(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.client.setState(NetClient.NetState.HANDSHAKE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        int resetIdx = buf.readerIndex();

        if (this.lastDecrypted == null) {
            this.lastDecrypted = ctx.alloc().buffer();
        }

        buf.readerIndex(this.lastDecrypted.writerIndex());

        int readableCrypted = buf.readableBytes();
        if (readableCrypted > 0) {
            NetCrypto crypto = this.client.getCryptoModule();
            if (crypto != null) {
                crypto.decrypt(buf, this.lastDecrypted, readableCrypted);
            } else {
                this.lastDecrypted.writeBytes(buf);
            }
        }

        int numRead = 0;
        int fullLen = 0;
        byte read;
        do {
            if (this.lastDecrypted.readableBytes() == 0) {
                buf.readerIndex(resetIdx);
                this.lastDecrypted.readerIndex(resetIdx);
                return;
            }

            read = this.lastDecrypted.readByte();
            int value = read & 0x7f;
            fullLen |= value << 7 * numRead;

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0);

        if (fullLen > this.lastDecrypted.readableBytes()) {
            buf.readerIndex(resetIdx);
            this.lastDecrypted.readerIndex(resetIdx);
            return;
        }

        ByteBuf decompressed;
        if (this.client.doCompression()) { // compression enabled
            int uncompressed = NetData.rvint(this.lastDecrypted);
            if (uncompressed != 0) {
                if (uncompressed < VeraServer.cfg().compressionThresh()) {
                    this.client.disconnect("Incorrect compression header");
                    return;
                }

                decompressed = ctx.alloc().buffer();
                byte[] in = NetData.arr(this.lastDecrypted, fullLen - BigInteger.valueOf(uncompressed).toByteArray().length);

                this.inflater.setInput(in);

                byte[] buffer = new byte[NetClient.BUFFER_SIZE];
                while (!this.inflater.finished()) {
                    int bytes = this.inflater.inflate(buffer);
                    decompressed.writeBytes(buffer, 0, bytes);
                }
                this.inflater.reset();
            } else { // compression enabled, < compress thresh
                decompressed = this.lastDecrypted.readBytes(fullLen - OutEncoder.VINT_LEN);
            }
        } else { // not compressed
            decompressed = this.lastDecrypted.readBytes(fullLen);
        }

        try {
            // Step 3: Decode packet
            int id = NetData.rvint(decompressed);

            Class<? extends Packet> cls = PacketRegistry.byId(this.client.getState(), Packet.Bound.SERVER, id);
            if (cls == null) {
                String stringId = String.format("%2s", Integer.toHexString(id).toUpperCase()).replace(' ', '0');
                VeraPlayer player = this.client.getPlayer();
                if (player != null) {
                    player.sendMessage("Packet 0x" + stringId + " => SERVER is not supported at this time");
                }
                LOGGER.warn("Client @ " + ctx.channel().remoteAddress() + " sent unsupported packet 0x" + stringId);
                return;
            }

            PacketIn packet = PacketRegistry.make(cls);

            LOGGER.debug("RECV: " + packet.getClass().getSimpleName());
            packet.read(decompressed, this.client);
        } finally {
            decompressed.release();

            if (this.lastDecrypted.readableBytes() == 0) {
                this.lastDecrypted.release();
                this.lastDecrypted = null;
            } else {
                buf.readerIndex(this.lastDecrypted.readerIndex());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.client != null) {
            this.client.disconnect("Server error: " + cause.getMessage());
        } else {
            ctx.channel().close().addListener(future -> LOGGER.error(ctx.channel().remoteAddress() + " disconnected due to server error"));
        }

        throw new RuntimeException(cause);
    }
}