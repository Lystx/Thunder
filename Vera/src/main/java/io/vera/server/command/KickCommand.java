
package io.vera.server.command;

import io.vera.server.VeraServer;
import io.vera.ui.chat.ChatComponent;
import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.annotation.PermissionRequired;
import io.vera.entity.living.Player;

import javax.annotation.concurrent.Immutable;

@Immutable
public class KickCommand implements CommandListener {

    @Command(name = "kick", help = "/kick <player> [reason]", desc = "Kicks a player from the server")
    @PermissionRequired("minecraft.kick")
    public void kick(CommandSource source, String[] args, Player player, String... reason) {


        if (player != null) {
            String reasonString = reason.length == 0 ? "Kicked by an operator." : String.join(" ", reason);
            player.kick(ChatComponent.text(reasonString));
            VeraServer.getInstance().getLogger().log("Kicked player " + player.getName() + " for: " + reasonString);
        } else {
            source.sendMessage(ChatComponent.text("No player by the name '" + args[1] + "' is online."));
        }
    }
}
