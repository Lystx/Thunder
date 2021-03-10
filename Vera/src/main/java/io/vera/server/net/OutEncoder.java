
package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.vera.logger.Logger;
import io.vera.server.VeraServer;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.ThreadSafe;
import java.math.BigInteger;
import java.util.zip.Deflater;

import static io.vera.server.net.NetData.arr;
import static io.vera.server.net.NetData.wvint;

@ThreadSafe
public class OutEncoder extends MessageToByteEncoder<PacketOut> {

    private static final Logger LOGGER = Logger.get(OutEncoder.class);
    public static final int VINT_LEN = BigInteger.ZERO.toByteArray().length;
    private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
    private NetClient client;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.client = NetClient.get(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketOut msg, ByteBuf out) throws Exception {
        ByteBuf payload = ctx.alloc().buffer();
        try {
            wvint(payload, msg.id());
            msg.write(payload);

            ByteBuf buf = ctx.alloc().buffer();
            try {
                if (this.client.doCompression()) {
                    int len = payload.readableBytes();
                    if (len > VeraServer.cfg().compressionThresh()) {
                        this.writeDeflated(payload, buf, len);
                    } else {
                        this.writeCompressed(payload, buf);
                    }
                } else {
                    this.writeUncompressed(payload, buf);
                }

                NetCrypto crypto = this.client.getCryptoModule();
                if (crypto != null) {
                    crypto.encrypt(buf, out);
                } else {
                    out.writeBytes(buf);
                }
            } finally {
                buf.release();
            }
        } finally {
            payload.release();
        }
        LOGGER.debug("SEND: " + msg.getClass().getSimpleName());
    }

    private void writeDeflated(ByteBuf payload, ByteBuf out, int len) {
        byte[] input = arr(payload, len);

        this.deflater.setInput(input);
        this.deflater.finish();

        byte[] buffer = new byte[NetClient.BUFFER_SIZE];
        ByteBuf result = payload.alloc().buffer();
        while (!this.deflater.finished()) {
            int deflated = this.deflater.deflate(buffer);
            result.writeBytes(buffer, 0, deflated);
        }

        this.deflater.reset();

        int resultLen = result.readableBytes();
        wvint(out, resultLen + BigInteger.valueOf(len).toByteArray().length);
        wvint(out, len);
        out.writeBytes(result);

        result.release();
    }

    private void writeCompressed(ByteBuf payload, ByteBuf out) {
        wvint(out, VINT_LEN + payload.readableBytes());
        wvint(out, 0);
        out.writeBytes(payload);
    }

    private void writeUncompressed(ByteBuf payload, ByteBuf out) {
        wvint(out, payload.readableBytes());
        out.writeBytes(payload);
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