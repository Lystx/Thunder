package org.gravel;

import lombok.Getter;
import lombok.Setter;
import org.gravel.elements.login.LoginGui;
import org.gravel.handler.PacketHandlerChat;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.netty.ConnectionClient;
import org.gravel.library.manager.networking.netty.NettyConnection;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutGlobalInfo;
import org.gravel.library.manager.networking.packets.result.PacketQueryLogOut;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.manager.FileManager;
import org.gravel.manager.GravelUserManager;


@Getter @Setter
public class GravelClient {

    private final NettyConnection nettyClient;
    private final String host;
    private final int port;

    private final FileManager fileManager;

    private GravelUser user;

    private boolean shutdown;

    public GravelClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.nettyClient = new ConnectionClient(host, port);
        this.nettyClient.getPacketAdapter().registerAdapter(this);
        this.nettyClient.getPacketAdapter().registerAdapter(new PacketHandlerChat(this));
        this.nettyClient.start();

        GravelAPI.init(this.nettyClient);

        this.fileManager = new FileManager();
        new LoginGui(this.fileManager.getConfig().getBoolean("registered"), this);

        GravelAPI.getInstance().setUserManager(new GravelUserManager());

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @PacketHandler
    public void handle(PacketOutGlobalInfo packet) {
        ((GravelUserManager)GravelAPI.getInstance().getUserManager()).setUsers(packet.getGravelUsers());
    }

    public void shutdown() {
        if (!this.shutdown) {
            this.shutdown = true;
            GravelAPI.getInstance().sendQuery(new PacketQueryLogOut(this.user));
            System.exit(0);
        }
    }
}
