package org.gravel;

import lombok.Getter;
import org.gravel.handler.PacketHandlerChat;
import org.gravel.handler.PacketHandlerResult;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.netty.ConnectionServer;
import org.gravel.library.manager.networking.netty.NettyConnection;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.manager.FileManager;
import org.gravel.manager.GravelAccountManager;
import org.gravel.manager.GravelChatManager;
import org.gravel.manager.GravelUserManager;

import java.util.*;


@Getter
public class GravelServer {

    private final NettyConnection nettyServer;
    private final FileManager fileManager;

    public GravelServer() {
        System.out.println("\n" +
                "   _____                     _  _____                          \n" +
                "  / ____|                   | |/ ____|                         \n" +
                " | |  __ _ __ __ ___   _____| | (___   ___ _ ____   _____ _ __ \n" +
                " | | |_ | '__/ _` \\ \\ / / _ \\ |\\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|\n" +
                " | |__| | | | (_| |\\ V /  __/ |____) |  __/ |   \\ V /  __/ |   \n" +
                "  \\_____|_|  \\__,_| \\_/ \\___|_|_____/ \\___|_|    \\_/ \\___|_|   \n" +
                "                                                               \n" +
                "                                                               ");
        System.out.println("---------------------------------");
        System.out.println("[Server] Loading GravelServer V1 by Lystx....");
        System.out.println("[Server] Starting NettyServer and waiting for Clients to connect!");
        System.out.println("[Server] Finished Booting up!");

        this.fileManager = new FileManager(this);

        this.nettyServer = new ConnectionServer("127.0.0.1", 1758);
        this.nettyServer.start();

        this.nettyServer.getPacketAdapter().registerAdapter(new PacketHandlerResult(this));
        this.nettyServer.getPacketAdapter().registerAdapter(new PacketHandlerChat());

        GravelAPI.init(this.nettyServer);

        GravelAPI.getInstance().setAccountManager(new GravelAccountManager(this.fileManager.getAccountFile()));
        GravelAPI.getInstance().setUserManager(new GravelUserManager(this.fileManager.getUserFile()));
        GravelAPI.getInstance().setChatManager(new GravelChatManager(this.fileManager.getChatFile()));

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

    }


    public void shutdown() {
        for (GravelUser user : GravelAPI.getInstance().getUserManager().getUsers()) {
            user.setStatus(UserStatus.OFFLINE);
            GravelAPI.getInstance().getUserManager().update(user);

        }
    }
}
