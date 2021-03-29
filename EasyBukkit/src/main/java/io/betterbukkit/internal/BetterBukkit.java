package io.betterbukkit.internal;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.listener.*;
import io.betterbukkit.provider.util.PlayerUtils;
import io.betterbukkit.provider.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        EasyBukkit.getInstance().init();

        this.recachePlayers();

        Bukkit.getPluginManager().registerEvents(new BetterBukkitPlayerListenerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new BetterBukkitPlayerListenerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new BetterBukkitPlayerListenerLogin(), this);
        Bukkit.getPluginManager().registerEvents(new BetterBukkitPlayerListenerMove(), this);
        Bukkit.getPluginManager().registerEvents(new BetterBukkitPlayerListenerCommand(), this);

    }

    @Override
    public void onDisable() {
        EasyBukkit.getInstance().shutdown();
    }

    public void recachePlayers() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            io.betterbukkit.provider.player.Player player = Util.get(PlayerUtils.class).from(onlinePlayer);
            EasyBukkit.getInstance().getPlayers().add(player);
        }
    }

}
