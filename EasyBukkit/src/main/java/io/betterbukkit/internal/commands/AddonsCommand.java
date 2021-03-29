package io.betterbukkit.internal.commands;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.command.Command;
import io.betterbukkit.provider.item.Substance;
import io.betterbukkit.provider.player.CommandSender;
import io.betterbukkit.provider.player.Player;
import io.betterbukkit.provider.util.Util;

public class AddonsCommand {

    @Command(name = "addons", description = "Shows all addons", permission = "easybukkit.addons.command")
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
                player.getInventory().setArmor(Substance.DIAMOND);
                player.sendMessage(EasyBukkit.getInstance().getPrefix() + "§7Debug Executed!");
                return;
            }
            if (EasyBukkit.getInstance().getAddonProvider().getAddons().size() == 0) {
                player.sendMessage(EasyBukkit.getInstance().getPrefix() + "§cThere aren't any addons loaded at the moment!");
                return;
            }
            player.sendMessage(EasyBukkit.getInstance().getPrefix() + "§7Addons §8[§e" + EasyBukkit.getInstance().getAddonProvider().getAddons().size() + "§8]§8: §7" + Util.toString(EasyBukkit.getInstance().getAddonProvider().getAddons()).toString().replace("[", "").replace("]", ""));

        }
    }
}
