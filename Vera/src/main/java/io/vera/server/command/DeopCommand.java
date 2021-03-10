
package io.vera.server.command;

import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.inventory.PlayerInventory;
import io.vera.server.ui.BossBar;
import io.vera.server.ui.Title;
import io.vera.ui.bossbar.BossBarColor;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;
import io.vera.command.*;
import io.vera.command.annotation.AllowedSourceTypes;
import io.vera.command.annotation.PermissionRequired;
import io.vera.command.annotation.PlayerExactMatch;
import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatType;

public class DeopCommand implements CommandListener {

    @Command(name = "deop", help = "/deop <player>", desc = "Sets the player to a non-operator") @PermissionRequired("minecraft.deop") @AllowedSourceTypes({ CommandSourceType.PLAYER, CommandSourceType.CONSOLE })
    public void deop(CommandSource source, String[] args, @PlayerExactMatch Player player) {
        if (player == null) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No player by the name '" + args[1] + "' is online!"));
        } else {
            player.setOp(false);
        }
    }
}
