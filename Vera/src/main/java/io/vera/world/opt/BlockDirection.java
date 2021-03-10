
package io.vera.world.opt;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Getter
@Immutable
public enum BlockDirection {
    NORTH(2, 0, 0, -1),
    NORTH_EAST(-1, 1, 0, -1),
    EAST(5, 1, 0, 0),
    SOUTH_EAST(-1, 1, 0, 1),
    SOUTH(3, 0, 0, 1),
    SOUTH_WEST(-1, -1, 0, 1),
    WEST(4, -1, 0, 0),
    NORTH_WEST(-1, -1, 0, -1),

    UP(1, 0, 1, 0),
    DOWN(0, 0, -1, 0);

    private final int minecraftDirection;

    private final int xDiff;

    private final int yDiff;
    private final int zDiff;

    BlockDirection(int minecraftDirection, int xDiff, int yDiff, int zDiff) {
        this.minecraftDirection = minecraftDirection;
        this.xDiff = xDiff;
        this.yDiff = yDiff;
        this.zDiff = zDiff;
    }

    public boolean hasMinecraftDirection() {
        return this.minecraftDirection != -1;
    }


    public BlockDirection anticlockwise() {
        return this.anticlockwise(false);
    }


    public BlockDirection anticlockwise(boolean includeDiagonals) {
        if (this.ordinal() >= 8)
            return this;
        return values()[(this.ordinal() + (includeDiagonals ? 7 : 6)) % 8];
    }

    public BlockDirection clockwise() {
        return this.clockwise(false);
    }

    public BlockDirection clockwise(boolean includeDiagonals) {
        if (this.ordinal() >= 8)
            return this;
        return values()[(this.ordinal() + (includeDiagonals ? 1 : 2)) % 8];
    }

    public BlockDirection getOpposite() {
        if (this.ordinal() < 8)
            return BlockDirection.values()[(this.ordinal() + 4) % 8];
        return this == UP ? DOWN : UP;
    }

    @Nonnull
    public static BlockDirection fromMinecraftDirection(int direction) {
        for (BlockDirection d : values()) {
            if(d.hasMinecraftDirection() && d.minecraftDirection == direction){
                return d;
            }
        }

        throw new IllegalArgumentException("no block direction with direction=" + direction);
    }
}