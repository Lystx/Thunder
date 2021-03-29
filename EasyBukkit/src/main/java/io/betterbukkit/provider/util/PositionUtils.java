package io.betterbukkit.provider.util;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.world.Block;
import io.betterbukkit.provider.world.Position;
import io.betterbukkit.provider.world.World;
import javafx.geometry.Pos;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class PositionUtils extends Util<Position, Location> {

    @Override
    public Position from(Location location) {
        return new Position() {
            @Override
            public Map<String, Object> convert() {
                Map<String, Object> map = new HashMap<>();
                map.put("x", getX());
                map.put("y", getY());
                map.put("z", getZ());
                map.put("yaw", getYaw());
                map.put("pitch", getPitch());
                map.put("world", getWorld().getName());
                return map;
            }

            @Override
            public Position deconvert(Map<String, Object> map) {
                return Position.create(EasyBukkit.getInstance().getWorld((String) map.get("world")), (double) map.get("x"), (double) map.get("y"), (double) map.get("z"), Float.parseFloat(map.get("yaw") + ""), Float.parseFloat(map.get("pitch") + ""));
            }

            @Override
            public double getX() {
                return location.getX();
            }

            @Override
            public double getY() {
                return location.getY();
            }

            @Override
            public double getZ() {
                return location.getZ();
            }

            @Override
            public float getYaw() {
                return location.getYaw();
            }

            @Override
            public float getPitch() {
                return location.getPitch();
            }

            @Override
            public World getWorld() {
                return get(WorldUtils.class).from(location.getWorld());
            }

            @Override
            public Block getBlockBelow() {
                return Util.get(BlockUtils.class).from(location.getWorld().getBlockAt(location.add(0, -1, 0)));
            }

            @Override
            public Block getBlock() {
                return Util.get(BlockUtils.class).from(location.getWorld().getBlockAt(location));
            }

            @Override
            public Position add(double x, double y, double z) {
                return get(PositionUtils.class).from(location.add(x, y, z));
            }
        };
    }

    @Override
    public Location to(Position position) {
        return new Location(
                get(WorldUtils.class).to(position.getWorld()),
                position.getX(),
                position.getY(),
                position.getZ(),
                position.getYaw(),
                position.getPitch()
        );
    }

}
