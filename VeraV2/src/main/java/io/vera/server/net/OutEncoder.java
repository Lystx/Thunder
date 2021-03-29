package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.vera.logger.Logger;
import io.vera.server.VeraServer;
import io.vera.server.packet.PacketOut;
import java.math.BigInteger;
import java.util.zip.Deflater;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class OutEncoder extends MessageToByteEncoder<PacketOut> {
  private static final Logger LOGGER = Logger.get(OutEncoder.class);
  
  public static final int VINT_LEN = (BigInteger.ZERO.toByteArray()).length;
  
  private final Deflater deflater = new Deflater(1);
  
  private NetClient client;
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.client = NetClient.get(ctx);
  }
  
  protected void encode(ChannelHandlerContext ctx, PacketOut msg, ByteBuf out) throws Exception {
    ByteBuf payload = ctx.alloc().buffer();
    try {
      NetData.wvint(payload, msg.id());
      msg.write(payload);
      ByteBuf buf = ctx.alloc().buffer();
      try {
        if (this.client.doCompression()) {
          int len = payload.readableBytes();
          if (len > VeraServer.cfg().compressionThresh()) {
            writeDeflated(payload, buf, len);
          } else {
            writeCompressed(payload, buf);
          } 
        } else {
          writeUncompressed(payload, buf);
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
    byte[] input = NetData.arr(payload, len);
    this.deflater.setInput(input);
    this.deflater.finish();
    byte[] buffer = new byte[8192];
    ByteBuf result = payload.alloc().buffer();
    while (!this.deflater.finished()) {
      int deflated = this.deflater.deflate(buffer);
      result.writeBytes(buffer, 0, deflated);
    } 
    this.deflater.reset();
    int resultLen = result.readableBytes();
    NetData.wvint(out, resultLen + (BigInteger.valueOf(len).toByteArray()).length);
    NetData.wvint(out, len);
    out.writeBytes(result);
    result.release();
  }
  
  private void writeCompressed(ByteBuf payload, ByteBuf out) {
    NetData.wvint(out, VINT_LEN + payload.readableBytes());
    NetData.wvint(out, 0);
    out.writeBytes(payload);
  }
  
  private void writeUncompressed(ByteBuf payload, ByteBuf out) {
    NetData.wvint(out, payload.readableBytes());
    out.writeBytes(payload);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (this.client != null) {
      this.client.disconnect("Server error: " + cause.getMessage());
    } else {
      ctx.channel().close().addListener(future -> LOGGER.error(ctx.channel().remoteAddress() + " disconnected due to server error"));
    } 
    throw new RuntimeException(cause);
  }
}
