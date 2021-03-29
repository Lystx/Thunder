package io.betterbukkit.provider.util;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.item.Item;
import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.world.Block;
import io.betterbukkit.provider.world.Position;
import io.betterbukkit.provider.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

public class WorldUtils extends Util<World, org.bukkit.World> {

    @Override
    public World from(org.bukkit.World world) {
        return new World() {
            @Override
            public String getName() {
                return world.getName();
            }

            @Override
            public Block getBlockAt(int var1, int var2, int var3) {
                return Util.get(BlockUtils.class).from(world.getBlockAt(var1, var2, var3));
            }

            @Override
            public Block getBlockAt(Position var1) {
                return getBlockAt((int)var1.getX(), (int)var1.getY(), (int)var1.getZ());
            }

            @Override
            public List<Player> getPlayers() {
                List<Player> list = new LinkedList<>();
                for (Player player : EasyBukkit.getInstance().getPlayers()) {
                    if (player.getWorld().getName().equalsIgnoreCase(getName())) {
                        list.add(player);
                    }
                }
                return list;
            }

            @Override
            public void dropItem(Item item, Position position) {
                world.dropItemNaturally(Util.get(PositionUtils.class).to(position), Util.get(ItemUtils.class).to(item));
            }
        };
    }

    @Override
    public org.bukkit.World to(World world) {
        return Bukkit.getWorld(world.getName());
    }

}
