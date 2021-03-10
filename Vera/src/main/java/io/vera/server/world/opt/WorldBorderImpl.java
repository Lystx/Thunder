
package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.world.World;
import io.vera.world.opt.WorldBorder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.vera.server.packet.play.PlayOutWorldBorder;
import io.vera.server.player.RecipientSelector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@RequiredArgsConstructor
public class WorldBorderImpl implements WorldBorder {

    private final World world;

    @Getter
    private volatile DoubleXZ center = DEFAULT_CENTER;
    private final AtomicLong size = new AtomicLong(Double.doubleToLongBits(DEFAULT_SIZE));
    private final AtomicLong targetSize = new AtomicLong(Double.doubleToLongBits(DEFAULT_SIZE));
    private final AtomicLong sizeTime = new AtomicLong(0);
    private final AtomicLong damage = new AtomicLong(Double.doubleToLongBits(DEFAULT_DAMAGE));
    private final AtomicLong safeZoneDistance = new AtomicLong(Double.doubleToLongBits(DEFAULT_SAFE_AND_WARN_DIST));
    private final AtomicInteger warn = new AtomicInteger(DEFAULT_SAFE_AND_WARN_DIST);
    private final AtomicInteger warnTime = new AtomicInteger(DEFAULT_WARN_TIME);

    @Override
    public void init() {
        DoubleXZ xz = this.center;

        RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.Init(xz.getX(), xz.getZ(), DEFAULT_SIZE,
                this.getSize(), this.getTargetTime(), this.warnTime.get(), this.warn.get()));
    }

    @Override
    public void setCenter(DoubleXZ center) {
        this.center = center;

        RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetCenter(center.getX(), center.getZ()));
    }

    @Override
    public double getSize() {
        return Double.longBitsToDouble(this.size.get());
    }

    @Override
    public double getTargetSize() {
        return Double.longBitsToDouble(this.targetSize.get());
    }

    @Override
    public long getTargetTime() {
        return this.sizeTime.get();
    }

    @Override
    public void setSize(double size, long time) {
        this.sizeTime.set(time);
        this.targetSize.set(Double.doubleToLongBits(size));

        if (time == 0) {
            RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetSize(size));
        } else {
            RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.LerpSize(Double.longBitsToDouble(this.size.get()), size, time));
        }
    }

    @Override
    public void grow(double delta, long time) {
        this.sizeTime.set(time);
        long oldSize;
        long newSize;
        double currentSize;
        double nextSize;
        do {
            oldSize = this.targetSize.get();
            currentSize = Double.longBitsToDouble(oldSize);
            nextSize = currentSize + delta;
            newSize = Double.doubleToLongBits(nextSize);
        }
        while (!this.targetSize.compareAndSet(oldSize, newSize));

        if (time == 0) {
            RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetSize(nextSize));
        } else {
            RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.LerpSize(currentSize, nextSize, time));
        }
    }

    @Override
    public double getDamage() {
        return Double.longBitsToDouble(this.damage.get());
    }

    @Override
    public void setDamage(double damage) {
        this.damage.set(Double.doubleToLongBits(damage));
    }

    @Override
    public double getSafeZoneDistance() {
        return Double.longBitsToDouble(this.safeZoneDistance.get());
    }

    @Override
    public void setSafeZoneDistance(int size) {
        this.safeZoneDistance.set(Double.doubleToLongBits(size));
    }

    @Override
    public int getWarnDistance() {
        return this.warn.get();
    }

    @Override
    public void setWarnDistance(int dist) {
        this.warn.set(dist);

        RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetWarnBlocks(dist));
    }

    @Override
    public void growWarnDistance(int dist) {
        int oldWarn;
        int newWarn;
        do {
            oldWarn = this.warn.get();
            newWarn = oldWarn + dist;
        }
        while (!this.warn.compareAndSet(oldWarn, newWarn));

        RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetWarnBlocks(newWarn));
    }

    @Override
    public int getWarnTime() {
        return this.warnTime.get();
    }

    @Override
    public void setWarnTime(int seconds) {
        this.warnTime.set(seconds);

        RecipientSelector.inWorld(this.world, new PlayOutWorldBorder.SetWarnTime(seconds));
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
            if (Double.compare(newSize, target) == 0) {
                break;
            }

            long period = prevTime;
            if (prevTime == 0) {
                period = 50;
            }

            double diff = target - newSize;
            long ticksUntilDone = period / 50;
            double delta = diff / ticksUntilDone;

            newSize = Math.max(0, newSize + delta);
            nextTime = Math.max(0, prevTime - 50);
        }
        while (!this.sizeTime.compareAndSet(prevTime, nextTime) || !this.size.compareAndSet(oldSize, Double.doubleToLongBits(newSize)));
    }

    public void read(Compound compound) {
        this.center = new DoubleXZ(compound.getDouble("BorderCenterX"), compound.getDouble("BorderCenterZ"));
        this.size.set(Double.doubleToLongBits(compound.getDouble("BorderSize")));
        this.targetSize.set(Double.doubleToLongBits(compound.getDouble("BorderSizeLerpTarget")));
        this.sizeTime.set(compound.getLong("BorderSizeLerpTime"));
        this.damage.set(Double.doubleToLongBits(compound.getDouble("BorderDamagePerBlock")));
        this.safeZoneDistance.set(Double.doubleToLongBits(compound.getDouble("BorderSafeZone")));
        this.warn.set((int) compound.getDouble("BorderWarningBlocks"));
        this.warnTime.set((int) compound.getDouble("BorderWarningTime"));
    }

    public void write(Compound compound) {
        compound.putDouble("BorderCenterX", this.center.getX());
        compound.putDouble("BorderCenterZ", this.center.getZ());
        compound.putDouble("BorderSize", this.getSize());
        compound.putDouble("BorderSizeLerpTarget", this.getTargetSize());
        compound.putLong("BorderSizeLerpTime", this.getTargetTime());
        compound.putDouble("BorderDamagePerBlock", this.getDamage());
        compound.putDouble("BorderSafeZone", this.getSafeZoneDistance());
        compound.putDouble("BorderWarningBlocks", this.getWarnDistance());
        compound.putDouble("BorderWarningTime", this.getWarnTime());
    }
}