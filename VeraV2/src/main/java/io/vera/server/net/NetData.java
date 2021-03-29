package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Vector;
import io.vera.world.vector.AbstractVector;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class NetData {
  public static final Charset NET_CHARSET = StandardCharsets.UTF_8;
  
  public static byte[] arr(ByteBuf buf) {
    return arr(buf, buf.readableBytes());
  }
  
  public static byte[] arr(ByteBuf buf, int len) {
    byte[] bytes = new byte[len];
    buf.readBytes(bytes);
    return bytes;
  }
  
  public static String rstr(ByteBuf buf) {
    int len = rvint(buf);
    byte[] stringData = arr(buf, len);
    return new String(stringData, NET_CHARSET);
  }
  
  public static void wstr(ByteBuf buf, String s) {
    wvint(buf, s.length());
    buf.writeBytes(s.getBytes(NET_CHARSET));
  }
  
  public static int rvint(ByteBuf buf) {
    int result = 0;
    int indent = 0;
    int b = buf.readByte();
    while ((b & 0x80) == 128) {
      if (indent >= 21)
        throw new RuntimeException("Too many bytes for a VarInt32."); 
      result += (b & 0x7F) << indent;
      indent += 7;
      b = buf.readByte();
    } 
    result += (b & 0x7F) << indent;
    return result;
  }
  
  public static void wvint(ByteBuf buf, int i) {
    while ((i & 0xFFFFFF80) != 0L) {
      buf.writeByte(i & 0x7F | 0x80);
      i >>>= 7;
    } 
    buf.writeByte(i & 0x7F);
  }
  
  public static byte convertAngle(float angle) {
    return (byte)(int)(angle / 1.40625D);
  }
  
  public static long rvlong(ByteBuf buf) {
    long result = 0L;
    int indent = 0;
    long b = buf.readByte();
    while ((b & 0x80L) == 128L) {
      if (indent >= 48)
        throw new RuntimeException("Too many bytes for a VarInt64."); 
      result += (b & 0x7FL) << indent;
      indent += 7;
      b = buf.readByte();
    } 
    result += b & 0x7FL;
    return result << indent;
  }
  
  public static void wvlong(ByteBuf buf, long l) {
    while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
      buf.writeByte((int)(l & 0x7FL | 0x80L));
      l >>>= 7L;
    } 
    buf.writeByte((int)(l & 0x7FL));
  }
  
  public static void rvec(ByteBuf buf, AbstractVector<?> vec) {
    long l = buf.readLong();
    vec.set((l >> 38L), (l >> 26L & 0xFFFL), (l << 38L >> 38L));
  }
  
  public static void wvec(ByteBuf buf, AbstractVector<?> vec) {
    long l = (vec.getIntX() & 0x3FFFFFFL) << 38L | (vec.getIntY() & 0xFFFL) << 26L | vec.getIntZ() & 0x3FFFFFFL;
    buf.writeLong(l);
  }
  
  public static Vector rvec(ByteBuf buf) {
    long pos = buf.readLong();
    return new Vector((pos >> 38L), (pos >> 26L & 0xFFFL), (pos << 38L >> 38L));
  }
}
