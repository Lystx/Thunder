package io.betterbukkit.provider.world;

import io.betterbukkit.elements.Convertable;
import io.betterbukkit.provider.item.Item;
import io.betterbukkit.provider.util.PositionUtils;
import io.betterbukkit.provider.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;

public interface Position extends Convertable<Position> {

    double getX();

    double getY();

    double getZ();

    float getYaw();

    default int toBlock(double loc) {
        return NumberConversions.floor(loc);
    }

    float getPitch();

    World getWorld();

    Block getBlockBelow();

    Block getBlock();

    Position add(double x, double y, double z);

    default void dropItem(Item item) {
        this.getWorld().dropItem(item, this);
    }

    static Position create() {
        return Util.get(PositionUtils.class).from(new Location(Bukkit.getWorld("world"), 0, 0, 0));
    }

    static Position create(World world, double x, double y, double z) {
        return Util.get(PositionUtils.class).from(new Location(Bukkit.getWorld(world.getName()), x, y, z));
    }
    static Position create(World world, double x, double y, double z, float yaw, float pitch) {
        return Util.get(PositionUtils.class).from(new Location(Bukkit.getWorld(world.getName()), x, y, z, yaw, pitch));
    }

}
