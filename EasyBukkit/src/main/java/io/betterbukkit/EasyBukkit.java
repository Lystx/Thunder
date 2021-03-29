package io.betterbukkit;

import io.betterbukkit.internal.commands.AddonsCommand;
import io.betterbukkit.provider.addon.AddonProvider;
import io.betterbukkit.provider.command.CommandProvider;
import io.betterbukkit.provider.event.EventProvider;
import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.scheduler.Scheduler;
import io.betterbukkit.provider.util.*;
import io.betterbukkit.provider.world.World;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class EasyBukkit {

    private static EasyBukkit instance;

    private final List<Player> players;
    private final AddonProvider addonProvider;
    private final EventProvider eventProvider;
    private final CommandProvider commandProvider;
    private final Scheduler scheduler;

    public EasyBukkit() {
        this.players = new LinkedList<>();

        this.addonProvider = new AddonProvider(this);
        this.eventProvider = new EventProvider(this);
        this.commandProvider = new CommandProvider(this);
        this.scheduler = new Scheduler(this);

        Util.registerUtil(new PlayerUtils());
        Util.registerUtil(new WorldUtils());
        Util.registerUtil(new PositionUtils());
        Util.registerUtil(new ItemUtils());
        Util.registerUtil(new PlayerInventoryUtils());
        Util.registerUtil(new BlockUtils());

    }

    public void init() {
        this.addonProvider.loadAddons();
        this.addonProvider.enableAddons();

        this.commandProvider.registerCommand(new AddonsCommand());
    }

    public void shutdown() {
        this.addonProvider.disableAddons();
    }

    public Player getPlayer(String name) {
        return this.players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public World getWorld(String name) {
        return Util.get(WorldUtils.class).from(Bukkit.getWorld(name));
    }

    public Player getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public String getPrefix() {
        return "§8[§eEasyBukkit§8] §7";
    }

    public static EasyBukkit getInstance() {
        if (instance == null) {
            instance = new EasyBukkit();
        }
        return instance;
    }
}
