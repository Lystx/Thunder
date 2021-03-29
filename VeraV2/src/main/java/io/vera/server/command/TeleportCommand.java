package io.vera.server.command;

import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.command.annotation.AllowedSourceTypes;
import io.vera.command.annotation.MaxCount;
import io.vera.command.annotation.PermissionRequired;
import io.vera.command.annotation.PlayerExactMatch;
import io.vera.entity.living.Player;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;
import io.vera.world.other.Position;

public class TeleportCommand implements CommandListener {
  @Command(name = "teleport", aliases = {"tp"}, help = "/teleport <player> <x> <y> <z> [<pitch> <yaw>]", desc = "Teleports the given player to the given XYZ")
  @PermissionRequired({"minecraft.tp"})
  @AllowedSourceTypes({CommandSourceType.PLAYER})
  public void teleport(CommandSource source, String[] args, @PlayerExactMatch Player player, double x, double y, double z, @MaxCount(2) float... direction) {
    if (player == null) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No player by the name '" + args[1] + "' is online"));
    } else {
      float pitch = (direction.length > 0) ? direction[0] : 0.0F;
      float yaw = (direction.length > 1) ? direction[1] : 0.0F;
      player.setPosition(new Position(player.getWorld(), x, y, z, pitch, yaw));
    } 
  }
}
