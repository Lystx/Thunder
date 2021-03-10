
package io.vera.server.command;

import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.server.VeraServer;
import io.vera.ui.chat.ChatComponent;
import io.vera.ui.chat.ClickAction;
import io.vera.ui.chat.ClickEvent;
import io.vera.command.annotation.MinCount;
import io.vera.command.annotation.PermissionRequired;
import io.vera.server.player.VeraPlayer;

public class SayCommand implements CommandListener {
    @Command(name = "say", help = "/say <message>", desc = "Broadcasts a message to all players")
    @PermissionRequired("minecraft.say")
    public void say(CommandSource source, String[] args, @MinCount(1) String... message) {
        StringBuilder builder = new StringBuilder();
        for (String arg : message) {
            builder.append(' ').append(arg);
        }

        if (source.getCmdType() == CommandSourceType.PLAYER) {
            String name = ((VeraPlayer) source).getName();
            String msg = '[' + name + "]";
            ChatComponent cc = ChatComponent.create()
                    .setText(msg)
                    .setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/tell " + name + " "))
                    .addExtra(builder.toString());
            for (VeraPlayer player : VeraPlayer.getPlayers().values()) {
                player.sendMessage(cc);
            }
            VeraServer.getInstance().getLogger().log(msg + builder);
        } else {
            String msg = "[Server]" + builder;
            for (VeraPlayer player : VeraPlayer.getPlayers().values()) {
                player.sendMessage(ChatComponent.create().setText(msg));
            }
            VeraServer.getInstance().getLogger().log(msg);
        }
    }
}
