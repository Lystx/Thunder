package io.betterbukkit.provider.util;

import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.player.PlayerInventory;
import io.betterbukkit.provider.world.Position;
import io.betterbukkit.provider.world.World;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.UUID;

public class PlayerUtils extends Util<Player, org.bukkit.entity.Player> {

    @Override
    public Player from(org.bukkit.entity.Player player) {
        return new Player() {
            @Override
            public String getName() {
                return player.getName();
            }

            @Override
            public UUID getUniqueId() {
                return player.getUniqueId();
            }

            @Override
            public void sendMessage(String message) {
                player.sendMessage(message);
            }

            @Override
            public void sendTitle(String title, String subtitle, long stay) {
                player.sendTitle(title, subtitle);
            }

            @Override
            public void sendActionbar(String message) {

            }

            @Override
            public void connect(String server) {

            }

            @Override
            public World getWorld() {
                return get(WorldUtils.class).from(player.getWorld());
            }

            @Override
            public Position getPosition() {
                return get(PositionUtils.class).from(player.getLocation());
            }

            @Override
            public void teleport(Position position) {
                player.teleport(get(PositionUtils.class).to(position));
            }

            @Override
            public double getHealth() {
                return player.getHealth();
            }

            @Override
            public int getFood() {
                return player.getFoodLevel();
            }

            @Override
            public void sendPacket(Object packet) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
            }

            @Override
            public void setFood(int level) {
                player.setFoodLevel(level);
            }

            @Override
            public void setHealth(double health) {
                player.setHealth(health);
            }

            @Override
            public void disconnect(String reason) {
                player.kickPlayer(reason);
            }

            @Override
            public boolean hasPermission(String permission) {
                return player.hasPermission(permission);
            }

            @Override
            public boolean isOp() {
                return player.isOp();
            }

            @Override
            public void setOp(boolean op) {
                player.setOp(true);
            }

            @Override
            public PlayerInventory getInventory() {
                return Util.get(PlayerInventoryUtils.class).from(player.getInventory());
            }
        };
    }

    @Override
    public org.bukkit.entity.Player to(Player player) {
        return Bukkit.getPlayer(player.getName());
    }

}
