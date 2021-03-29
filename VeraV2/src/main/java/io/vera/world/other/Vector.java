
package io.vera.world.other;

import io.vera.world.vector.AbstractVector;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Vector extends AbstractVector<Vector> {

    private static final long serialVersionUID = 9128045730182148574L;

    public Vector() {
        this(0D, 0D, 0D);
    }

    public Vector(int x, int y, int z) {
        this((double) x, (double) y, (double) z);
    }

    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public Vector setX(double x) {
        return new Vector(x, this.y, this.z);
    }

    @Override
    public Vector setY(double y) {
        return new Vector(this.x, y, this.z);
    }

    @Override
    public Vector setZ(double z) {
        return new Vector(this.x, this.y, z);
    }

    @Override
    public Vector set(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public double getMagnitudeSquared() {
        double x = this.x;
        double y = this.y;
        double z = this.z;

        return square(x) + square(y) + square(z);
    }

    public double getMagnitude() {
        return Math.sqrt(this.getMagnitudeSquared());
    }

    public Vector normalize() {
        double mag = this.getMagnitude();
        return this.divide(mag, mag, mag);
    }
}