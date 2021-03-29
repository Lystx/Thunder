package io.betterbukkit.provider.player;

import io.betterbukkit.provider.world.Position;
import io.betterbukkit.provider.world.World;

public interface Player extends CommandSender {

    void sendTitle(String title, String subtitle, long stay);

    default void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20L);
    }

    void sendActionbar(String message);

    void connect(String server);

    World getWorld();

    Position getPosition();

    void teleport(Position position);

    default void teleport(Player player) {
        this.teleport(player.getPosition());
    }

    double getHealth();

    int getFood();

    default void heal() {
        this.setFood(20);
        this.setHealth(20.0D);
    }

    void sendPacket(Object packet);

    void setFood(int level);

    void setHealth(double health);

    void disconnect(String reason);

    boolean isOp();

    void setOp(boolean op);

    PlayerInventory getInventory();
}
