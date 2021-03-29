package de.lystx.messenger;

import de.lystx.messenger.commands.StopCommand;
import de.lystx.messenger.handler.AccountHandler;
import de.lystx.messenger.handler.ChatHandler;
import de.lystx.messenger.handler.FriendHandler;
import de.lystx.messenger.handler.PacketHandlerResult;
import de.lystx.messenger.manage.FileManager;
import de.lystx.messenger.manager.FriendManager;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.networking.netty.ConnectionServer;
import lombok.Getter;

import java.io.File;

@Getter
public class Server {

    private final Console console;

    private final ConnectionServer connectionServer;
    private final FileManager fileManager;

    @Getter
    private static Server instance;

    public Server() {
        instance = this;
        this.fileManager = new FileManager();

        this.connectionServer = new ConnectionServer(
                (MessageAPI.getInstance().getHost() != null ? MessageAPI.getInstance().getHost() : "127.0.0.1"),
                (MessageAPI.getInstance().getPort() != null ? MessageAPI.getInstance().getPort() : 1758));
        this.connectionServer.getPacketAdapter().registerAdapter(new AccountHandler());
        this.connectionServer.getPacketAdapter().registerAdapter(new PacketHandlerResult());
        this.connectionServer.getPacketAdapter().registerAdapter(new FriendHandler());
        this.connectionServer.getPacketAdapter().registerAdapter(new ChatHandler());
        this.connectionServer.start();

        MessageAPI.getInstance().setFriendManager(new FriendManager(new File("./"), this.connectionServer));
        MessageAPI.getInstance().setNettyConnection(this.connectionServer);

        this.console = new Console("&eMSG@&6" + System.getProperty("user.name") + " &8» &f");
        this.console.sendMessage("\n" +
                "  __  __                                          \n" +
                " |  \\/  |                                         \n" +
                " | \\  / | ___  ___ ___  ___ _ __   __ _  ___ _ __ \n" +
                " | |\\/| |/ _ \\/ __/ __|/ _ \\ '_ \\ / _` |/ _ \\ '__|\n" +
                " | |  | |  __/\\__ \\__ \\  __/ | | | (_| |  __/ |   \n" +
                " |_|  |_|\\___||___/___/\\___|_| |_|\\__, |\\___|_|   \n" +
                "                                   __/ |          \n" +
                "                                  |___/           ");
        this.console.sendMessage("&8--------------------------------");
        this.console.sendMessage("NETWORK", "&fLoading &eNetty Components&8...");
        this.console.sendMessage("NETWORK", "&fStarted &eNettyServer &fon &e" + this.connectionServer.getHost() + "&8:&e" + this.connectionServer.getPort() + "&8!");

        MessageAPI.getInstance().init(this.console);

        MessageAPI.getInstance().getCommandManager().registerCommand(new StopCommand("stop", "Stoppt den Server", "exit", "shutdown"));

    }

    public void shutdown() {
        this.console.stop();
        this.connectionServer.disconnect();
        System.exit(-1);
    }

}
