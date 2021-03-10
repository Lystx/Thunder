
package io.vera.server.command;

import io.vera.server.VeraServer;
import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.command.annotation.AllowedSourceTypes;
import io.vera.command.annotation.PermissionRequired;

public class StopCommand implements CommandListener {

    @Command(name = "stop", aliases = { "shutdown", "fuck" }, help = "/stop", desc = "Stops the server and shuts-down")
    @PermissionRequired("minecraft.stop")
    @AllowedSourceTypes({ CommandSourceType.CONSOLE, CommandSourceType.PLAYER })
    public void stop(CommandSource source, String[] args) {
        VeraServer.getInstance().shutdown();
    }
}
