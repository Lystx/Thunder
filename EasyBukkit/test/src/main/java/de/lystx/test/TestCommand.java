package de.lystx.test;

import io.betterbukkit.provider.command.Command;
import io.betterbukkit.provider.player.CommandSender;
import io.betterbukkit.provider.player.Player;

public class TestCommand {


    @Command(
            name = "test",
            description = "Das ist ein Test",
            aliases = {"t"}
    )
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage("§7Test ausgefüht§8!");
        }
    }
}
