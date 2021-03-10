
package io.vera.world.opt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface WorldBorder {

    DoubleXZ DEFAULT_CENTER = new DoubleXZ(0, 0);

    double DEFAULT_SIZE = 60_000_000;
    int DEFAULT_SAFE_AND_WARN_DIST = 5;
    int DEFAULT_WARN_TIME = 15;

    double DEFAULT_DAMAGE = 0.2;


    @Getter @Setter @AllArgsConstructor
    class DoubleXZ {
        private final double x;
        private final double z;
    }

    void init();


    DoubleXZ getCenter();

    void setCenter(DoubleXZ center);

    double getSize();

    double getTargetSize();

    long getTargetTime();

    void setSize(double size, long time);

    void grow(double delta, long time);

    double getDamage();

    void setDamage(double damage);

    double getSafeZoneDistance();

    void setSafeZoneDistance(int size);

    int getWarnDistance();

    void setWarnDistance(int dist);

    void growWarnDistance(int dist);

    int getWarnTime();

    void setWarnTime(int seconds);
}