package org.gravel;

import lombok.Getter;
import lombok.Setter;
import org.gravel.elements.gui.login.LoginGui;
import org.gravel.elements.gui.screen.MainGUi;
import org.gravel.handler.PacketHandlerChat;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.netty.ConnectionClient;
import org.gravel.library.manager.networking.packet.PacketHandler;
import org.gravel.library.manager.networking.packets.out.*;
import org.gravel.library.manager.networking.packets.result.PacketQueryLogOut;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.manager.FileManager;
import org.gravel.manager.GravelAccountManager;
import org.gravel.manager.GravelUserManager;

import java.util.LinkedList;
import java.util.List;


@Getter @Setter
public class GravelClient {

    private final ConnectionClient nettyClient;
    private final String host;
    private final int port;

    private final FileManager fileManager;

    private GravelUser user;
    private boolean shutdown;
    private List<Chat> chats;

    private boolean typing;

    public GravelClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.chats = new LinkedList<>();

        this.nettyClient = new ConnectionClient(host, port);
        this.nettyClient.getPacketAdapter().registerAdapter(this);
        this.nettyClient.getPacketAdapter().registerAdapter(new PacketHandlerChat(this));
        this.nettyClient.start();

        GravelAPI.init(this.nettyClient);

        this.fileManager = new FileManager();
        new LoginGui(this.fileManager.getConfig().getBoolean("registered"), this);

        GravelAPI.getInstance().setUserManager(new GravelUserManager());
        GravelAPI.getInstance().setAccountManager(new GravelAccountManager(this));

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @PacketHandler
    public void handle(PacketOutGlobalInfo packet) {
        ((GravelUserManager)GravelAPI.getInstance().getUserManager()).setUsers(packet.getGravelUsers());
        ((GravelAccountManager)GravelAPI.getInstance().getAccountManager()).setAccounts(packet.getAccounts());
    }

    @PacketHandler
    public void handle(PacketOutNotify packet) {
        try {
            if (!packet.getGravelUser().getAccount().getName().equalsIgnoreCase(this.user.getAccount().getName())) {
                return;
            }
            GravelAPI.getInstance().sendNotification("Gravel | Messenger", packet.getTitle(), packet.getMessage());
        } catch (NullPointerException e) {

        }
    }

    @PacketHandler
    public void handle(PacketTyping packet) {
        final Chat chat = packet.getChat();
        final GravelUser gravelUser = packet.getGravelUser();
        if (chat.getName().equalsIgnoreCase(GravelAPI.getInstance().getCurrentChat().getName())) {
            //TODO: TYPING INDICATOR IN GROUP CHAT
            if (chat.isGroup()) {
                MainGUi.playerLabel.setText(chat.getName() + " [" + chat.getUsers().size() + " Users]");
            } else {
                final GravelUser otherMember = chat.getOtherMember(this.user);
                if (gravelUser.getAccount().getName().equalsIgnoreCase(this.user.getAccount().getName())) {
                    return;
                }
                MainGUi.playerLabel.setText(otherMember.getAccount().getName() + " is typing...");
                typing = true;
                GravelAPI.getInstance().schedule(() -> {
                    if (!typing) {
                        return;
                    }
                    typing = false;
                    MainGUi.playerLabel.setText(otherMember.getAccount().getName() + " [" + otherMember.getStatus() + "]");
                }, 40L);
            }
        }
    }

    @PacketHandler
    public void handle(PacketOutUpdatePlayer packet) {
        final GravelUser gravelUser = packet.getGravelUser();
        if (gravelUser == null || this.user == null) {
            return;
        }
        if (gravelUser.getAccount().getName().equalsIgnoreCase(this.user.getAccount().getName())) {
            this.user = gravelUser;
        }
    }

    public void shutdown() {
        if (!this.shutdown) {
            this.shutdown = true;
            GravelAPI.getInstance().sendQuery(new PacketQueryLogOut(this.user));
            System.exit(0);
        }
    }
}
