
package io.vera.world.vector;

import io.vera.server.world.World;
import io.vera.world.other.Position;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

@Immutable
public abstract class AbstractVector<T extends AbstractVector<T>> implements Serializable {
    private static final long serialVersionUID = 218773668333902972L;

    protected static boolean eq(double d0, double d1) {
        return Double.compare(d0, d1) == 0;
    }

    protected static double square(double d) {
        return d * d;
    }

    @Getter
    protected final double x;
    @Getter
    protected final double y;
    @Getter
    protected final double z;

    public AbstractVector() {
        this(0D, 0D, 0D);
    }

    public AbstractVector(int x, int y, int z) {
        this((double) x, (double) y, (double) z);
    }

    public AbstractVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getIntX() {
        return (int) this.x;
    }

    public int getIntY() {
        return (int) this.y;
    }

    public int getIntZ() {
        return (int) this.z;
    }

    public T setX(int x) {
        return this.setX((double) x);
    }


    public abstract T setX(double x);

    public T setY(int y) {
        return this.setY((double) y);
    }

    public abstract T setY(double y);

    public T setZ(int z) {
        return this.setZ((double) z);
    }

    public abstract T setZ(double z);

    public T set(int x, int y, int z) {
        return this.set((double) x, (double) y, (double) z);
    }

    public abstract T set(double x, double y, double z);

    public T add(T vector) {
        return this.set(this.x + vector.x, this.y + vector.y, this.z + vector.z);
    }

    public T add(int x, int y, int z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    public T add(double x, double y, double z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    public T subtract(T vector) {
        return this.set(this.x - vector.x, this.y - vector.y, this.z - vector.z);
    }

    public T subtract(int x, int y, int z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    public T subtract(double x, double y, double z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    public T multiply(T vector) {
        return this.set(this.x * vector.x, this.y * vector.y, this.z * vector.z);
    }

    public T multiply(int x, int y, int z) {
        return this.set(this.x * x, this.y * y, this.z * z);
    }

    public T multiply(double x, double y, double z) {
        return this.set(this.x * x, this.y * y, this.z * z);
    }

    public T divide(T vector) {
        return this.set(this.x / vector.x, this.y / vector.y, this.z / vector.z);
    }

    public T divide(int x, int y, int z) {
        return this.set(this.x / x, this.y / y, this.z / z);
    }

    public T divide(double x, double y, double z) {
        return this.set(this.x / x, this.y / y, this.z / z);
    }


    public Position toPosition(World world){
        return new Position(world, this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractVector) {
            AbstractVector<?> v = (AbstractVector<?>) obj;
            return eq(this.x, v.x) && eq(this.y, v.y) && eq(this.z, v.z);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 31 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 31 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }

    @Override
    public String toString() {
        return "Vector{" + this.x + ',' + this.y + ',' + this.z + '}';
    }
}