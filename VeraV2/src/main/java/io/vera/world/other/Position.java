
package io.vera.world.other;

import io.vera.server.world.Block;
import io.vera.server.world.World;
import io.vera.world.vector.AbstractVector;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;


@Immutable
public final class Position extends AbstractVector<Position> {
    private static final long serialVersionUID = 5910507790866074403L;

    @Getter
    private final World world;
    @Getter
    private final float yaw;
    @Getter
    private final float pitch;

    public Position(@Nonnull World world) {
        this(world, 0D, 0D, 0D, 0F, 0F);
    }

    public Position(@Nonnull World world, int x, int y, int z) {
        this(world, (double) x, (double) y, (double) z, 0F, 0F);
    }


    public Position(@Nonnull World world, double x, double y, double z) {
        this(world, x, y, z, 0F, 0F);
    }

    public Position(@Nonnull World world, double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.world = world;
        this.yaw = yaw > 360 || yaw < -360 ? yaw % 360 : yaw;
        this.pitch = pitch > 90 || pitch < -90 ? pitch % 90 : pitch;
    }

    public Position setWorld(World world) {
        return new Position(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public Position setYaw(float yaw) {
        return new Position(this.world, this.x, this.y, this.z, yaw, this.pitch);
    }

    public Position setPitch(float pitch) {
        return new Position(this.world, this.x, this.y, this.z, this.yaw, pitch);
    }


    public Block getBlock() {
        return this.world.getBlockAt(this);
    }

    public double distanceSquared(Position position) {
        double dX = this.x - position.x;
        double dY = this.y - position.y;
        double dZ = this.z - position.z;

        return square(dX) + square(dY) + square(dZ);
    }

    public double distance(Position position) {
        return Math.sqrt(this.distanceSquared(position));
    }

    private static boolean eq(float f0, float f1) {
        return Float.compare(f0, f1) == 0;
    }

    public int getChunkX() {
        return this.getIntX() >> 4;
    }

    public int getChunkZ() {
        return this.getIntZ() >> 4;
    }

    @Override
    public Position setX(double x) {
        return new Position(this.world, x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public Position setY(double y) {
        return new Position(this.world, this.x, y, this.z, this.yaw, this.pitch);
    }

    @Override
    public Position setZ(double z) {
        return new Position(this.world, this.x, this.y, z, this.yaw, this.pitch);
    }

    @Override
    public Position set(double x, double y, double z) {
        return new Position(this.world, x, y, z, this.yaw, this.pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position v = (Position) obj;
            return eq(this.x, v.x) && eq(this.y, v.y) && eq(this.z, v.z) &&
                    this.world.equals(v.world) &&
                    eq(this.pitch, v.pitch) && eq(this.yaw, v.yaw);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + this.world.hashCode();
        hash = 31 * hash + Float.floatToIntBits(this.pitch);
        hash = 31 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("Position{%s, (%f, %f, %f), pitch=%f, yaw=%f}",
                this.world.getName(), this.x, this.y, this.z, this.pitch, this.yaw);
    }
}