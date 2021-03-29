package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutWorldBorder;
import io.vera.server.player.RecipientSelector;
import io.vera.server.world.World;
import io.vera.world.opt.WorldBorder;
import java.beans.ConstructorProperties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WorldBorderImpl implements WorldBorder {
  private final World world;
  
  private volatile DoubleXZ center;
  
  private final AtomicLong size;
  
  private final AtomicLong targetSize;
  
  private final AtomicLong sizeTime;
  
  private final AtomicLong damage;
  
  private final AtomicLong safeZoneDistance;
  
  private final AtomicInteger warn;
  
  private final AtomicInteger warnTime;
  
  @ConstructorProperties({"world"})
  public WorldBorderImpl(World world) {
    this.center = DEFAULT_CENTER;
    this.size = new AtomicLong(Double.doubleToLongBits(6.0E7D));
    this.targetSize = new AtomicLong(Double.doubleToLongBits(6.0E7D));
    this.sizeTime = new AtomicLong(0L);
    this.damage = new AtomicLong(Double.doubleToLongBits(0.2D));
    this.safeZoneDistance = new AtomicLong(Double.doubleToLongBits(5.0D));
    this.warn = new AtomicInteger(5);
    this.warnTime = new AtomicInteger(15);
    this.world = world;
  }
  
  public DoubleXZ getCenter() {
    return this.center;
  }
  
  public void init() {
    DoubleXZ xz = this.center;
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.Init(xz.getX(), xz.getZ(), 6.0E7D, 
            getSize(), getTargetTime(), this.warnTime.get(), this.warn.get()) });
  }
  
  public void setCenter(DoubleXZ center) {
    this.center = center;
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetCenter(center.getX(), center.getZ()) });
  }
  
  public double getSize() {
    return Double.longBitsToDouble(this.size.get());
  }
  
  public double getTargetSize() {
    return Double.longBitsToDouble(this.targetSize.get());
  }
  
  public long getTargetTime() {
    return this.sizeTime.get();
  }
  
  public void setSize(double size, long time) {
    this.sizeTime.set(time);
    this.targetSize.set(Double.doubleToLongBits(size));
    if (time == 0L) {
      RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetSize(size) });
    } else {
      RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.LerpSize(Double.longBitsToDouble(this.size.get()), size, time) });
    } 
  }
  
  public void grow(double delta, long time) {
    long oldSize, newSize;
    double currentSize, nextSize;
    this.sizeTime.set(time);
    do {
      oldSize = this.targetSize.get();
      currentSize = Double.longBitsToDouble(oldSize);
      nextSize = currentSize + delta;
      newSize = Double.doubleToLongBits(nextSize);
    } while (!this.targetSize.compareAndSet(oldSize, newSize));
    if (time == 0L) {
      RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetSize(nextSize) });
    } else {
      RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.LerpSize(currentSize, nextSize, time) });
    } 
  }
  
  public double getDamage() {
    return Double.longBitsToDouble(this.damage.get());
  }
  
  public void setDamage(double damage) {
    this.damage.set(Double.doubleToLongBits(damage));
  }
  
  public double getSafeZoneDistance() {
    return Double.longBitsToDouble(this.safeZoneDistance.get());
  }
  
  public void setSafeZoneDistance(int size) {
    this.safeZoneDistance.set(Double.doubleToLongBits(size));
  }
  
  public int getWarnDistance() {
    return this.warn.get();
  }
  
  public void setWarnDistance(int dist) {
    this.warn.set(dist);
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetWarnBlocks(dist) });
  }
  
  public void growWarnDistance(int dist) {
    int oldWarn, newWarn;
    do {
      oldWarn = this.warn.get();
      newWarn = oldWarn + dist;
    } while (!this.warn.compareAndSet(oldWarn, newWarn));
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetWarnBlocks(newWarn) });
  }
  
  public int getWarnTime() {
    return this.warnTime.get();
  }
  
  public void setWarnTime(int seconds) {
    this.warnTime.set(seconds);
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutWorldBorder.SetWarnTime(seconds) });
  }
  
  public void tick() {
    long prevTime;
    long nextTime;
    long oldSize;
    double newSize;
    do {
      double target = Double.longBitsToDouble(this.targetSize.get());
      prevTime = this.sizeTime.get();
      oldSize = this.size.get();
      newSize = Double.longBitsToDouble(oldSize);
      if (Double.compare(newSize, target) == 0)
        break; 
      long period = prevTime;
      if (prevTime == 0L)
        period = 50L; 
      double diff = target - newSize;
      long ticksUntilDone = period / 50L;
      double delta = diff / ticksUntilDone;
      newSize = Math.max(0.0D, newSize + delta);
      nextTime = Math.max(0L, prevTime - 50L);
    } while (!this.sizeTime.compareAndSet(prevTime, nextTime) || !this.size.compareAndSet(oldSize, Double.doubleToLongBits(newSize)));
  }
  
  public void read(Compound compound) {
    this.center = new DoubleXZ(compound.getDouble("BorderCenterX"), compound.getDouble("BorderCenterZ"));
    this.size.set(Double.doubleToLongBits(compound.getDouble("BorderSize")));
    this.targetSize.set(Double.doubleToLongBits(compound.getDouble("BorderSizeLerpTarget")));
    this.sizeTime.set(compound.getLong("BorderSizeLerpTime"));
    this.damage.set(Double.doubleToLongBits(compound.getDouble("BorderDamagePerBlock")));
    this.safeZoneDistance.set(Double.doubleToLongBits(compound.getDouble("BorderSafeZone")));
    this.warn.set((int)compound.getDouble("BorderWarningBlocks"));
    this.warnTime.set((int)compound.getDouble("BorderWarningTime"));
  }
  
  public void write(Compound compound) {
    compound.putDouble("BorderCenterX", this.center.getX());
    compound.putDouble("BorderCenterZ", this.center.getZ());
    compound.putDouble("BorderSize", getSize());
    compound.putDouble("BorderSizeLerpTarget", getTargetSize());
    compound.putLong("BorderSizeLerpTime", getTargetTime());
    compound.putDouble("BorderDamagePerBlock", getDamage());
    compound.putDouble("BorderSafeZone", getSafeZoneDistance());
    compound.putDouble("BorderWarningBlocks", getWarnDistance());
    compound.putDouble("BorderWarningTime", getWarnTime());
  }
}
