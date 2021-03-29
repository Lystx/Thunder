package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class PlayOutWorldBorder extends PacketOut {
  public PlayOutWorldBorder() {
    super(PlayOutWorldBorder.class);
  }
  
  @Immutable
  public static class SetSize extends PlayOutWorldBorder {
    private final double diameter;
    
    public SetSize(double diameter) {
      this.diameter = diameter;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 0);
      buf.writeDouble(this.diameter);
    }
  }
  
  @Immutable
  public static class LerpSize extends PlayOutWorldBorder {
    private final double old;
    
    private final double newDiameter;
    
    private final long millis;
    
    public LerpSize(double old, double newDiameter, long millis) {
      this.old = old;
      this.newDiameter = newDiameter;
      this.millis = millis;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 1);
      buf.writeDouble(this.old);
      buf.writeDouble(this.newDiameter);
      NetData.wvlong(buf, this.millis);
    }
  }
  
  @Immutable
  public static class SetCenter extends PlayOutWorldBorder {
    private final double x;
    
    private final double z;
    
    public SetCenter(double x, double z) {
      this.x = x;
      this.z = z;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 2);
      buf.writeDouble(this.x);
      buf.writeDouble(this.z);
    }
  }
  
  @Immutable
  public static class Init extends PlayOutWorldBorder {
    private final double x;
    
    private final double z;
    
    private final double oldDiameter;
    
    private final double newDiameter;
    
    private final long millis;
    
    private final int warnTime;
    
    private final int warnBlocks;
    
    public Init(double x, double z, double oldDiameter, double newDiameter, long growthMillis, int warnTime, int warnBlocks) {
      this.x = x;
      this.z = z;
      this.oldDiameter = oldDiameter;
      this.newDiameter = newDiameter;
      this.millis = growthMillis;
      this.warnTime = warnTime;
      this.warnBlocks = warnBlocks;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 3);
      buf.writeDouble(this.x);
      buf.writeDouble(this.z);
      buf.writeDouble(this.oldDiameter);
      buf.writeDouble(this.newDiameter);
      NetData.wvlong(buf, this.millis);
      NetData.wvint(buf, 29999984);
      NetData.wvint(buf, this.warnTime);
      NetData.wvint(buf, this.warnBlocks);
    }
  }
  
  @Immutable
  public static class SetWarnTime extends PlayOutWorldBorder {
    private final int seconds;
    
    public SetWarnTime(int seconds) {
      this.seconds = seconds;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 4);
      NetData.wvint(buf, this.seconds);
    }
  }
  
  @Immutable
  public static class SetWarnBlocks extends PlayOutWorldBorder {
    private final int blocks;
    
    public SetWarnBlocks(int blocks) {
      this.blocks = blocks;
    }
    
    public void write(ByteBuf buf) {
      NetData.wvint(buf, 5);
      NetData.wvint(buf, this.blocks);
    }
  }
}
