
package io.vera.server.command;

import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;
import io.vera.command.*;
import io.vera.command.annotation.AllowedSourceTypes;
import io.vera.command.annotation.PermissionRequired;
import io.vera.command.annotation.PlayerExactMatch;
import io.vera.entity.living.Player;

public class OpCommand implements CommandListener {

    @Command(name = "op", help = "/op <player>", desc = "Sets the player to an operator")
    @PermissionRequired("minecraft.op")
    @AllowedSourceTypes({ CommandSourceType.PLAYER, CommandSourceType.CONSOLE })
    public void op(CommandSource source, String[] args, @PlayerExactMatch Player player) {
        if (player == null) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No player by the name '" + args[1] + "' is online!"));
        } else {
            player.setOp(true);
        }
    }
}
