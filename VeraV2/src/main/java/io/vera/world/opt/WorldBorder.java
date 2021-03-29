package io.vera.world.opt;

import java.beans.ConstructorProperties;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface WorldBorder {
    public static final DoubleXZ DEFAULT_CENTER = new DoubleXZ(0.0D, 0.0D);

    public static final double DEFAULT_SIZE = 6.0E7D;

    public static final int DEFAULT_SAFE_AND_WARN_DIST = 5;

    public static final int DEFAULT_WARN_TIME = 15;

    public static final double DEFAULT_DAMAGE = 0.2D;

    void init();

    DoubleXZ getCenter();

    void setCenter(DoubleXZ paramDoubleXZ);

    double getSize();

    double getTargetSize();

    long getTargetTime();

    void setSize(double paramDouble, long paramLong);

    void grow(double paramDouble, long paramLong);

    double getDamage();

    void setDamage(double paramDouble);

    double getSafeZoneDistance();

    void setSafeZoneDistance(int paramInt);

    int getWarnDistance();

    void setWarnDistance(int paramInt);

    void growWarnDistance(int paramInt);

    int getWarnTime();

    void setWarnTime(int paramInt);

    public static class DoubleXZ {
        private final double x;

        private final double z;

        @ConstructorProperties({"x", "z"})
        public DoubleXZ(double x, double z) {
            this.x = x;
            this.z = z;
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }
    }
}
