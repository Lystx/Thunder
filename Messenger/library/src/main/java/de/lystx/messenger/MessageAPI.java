package de.lystx.messenger;

import de.lystx.messenger.manager.FriendManager;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.account.AccountManager;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.chats.ChatManager;
import de.lystx.messenger.manager.command.CommandManager;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.networking.connection.adapter.PacketHandlerAdapter;
import de.lystx.messenger.networking.connection.packet.Packet;
import de.lystx.messenger.networking.connection.packet.PacketState;
import de.lystx.messenger.networking.netty.NettyConnection;
import de.lystx.messenger.networking.packets.Result;
import de.lystx.messenger.networking.packets.PacketInBoundHandler;
import de.lystx.messenger.util.Value;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


@Getter
public class MessageAPI {

    private static MessageAPI instance;

    private final AccountManager accountManager;
    private final CommandManager commandManager;
    private final ChatManager chatManager;

    @Setter
    private FriendManager friendManager;

    @Getter @Setter
    private NettyConnection nettyConnection;

    @Setter
    private List<Account> accounts;

    @Getter @Setter
    private Chat currentChat;

    @Setter
    private Account account;

    @Setter
    private String host;

    @Setter
    private Integer port;

    @Setter @Getter
    private Console console;

    @Setter @Getter
    private List<Chat> chats;

    public MessageAPI() {
        this.chats = new LinkedList<>();
        this.accounts = new LinkedList<>();
        this.accountManager = new AccountManager();
        this.commandManager = new CommandManager();
        this.chatManager = new ChatManager();
    }


    @SneakyThrows
    public void sendNotification(String toolTip, String caption, String text) {
        if (System.getProperty("os.name").startsWith("Win")) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, toolTip);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(toolTip);
            tray.add(trayIcon);

            trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
        } else {
            Runtime.getRuntime().exec(new String[] { "osascript", "-e", "display notification \"" + text + "\" with title \"" + toolTip + "\" subtitle \"" + caption + "\" sound name \"Crystal\"" });
        }
    }

    public void init(Console console) {
        this.commandManager.setConsole(console);
    }

    public Account getAccount(String name) {
        return this.accounts.stream().filter(account -> account.getName().trim().equalsIgnoreCase(name.trim())).findFirst().orElse(null);
    }

    public <T> Result<T> sendQuery(PacketInBoundHandler<T> packet) {
        return this.sendQuery(this.nettyConnection, packet);
    }

    public <T> Result<T> sendQuery(NettyConnection nettyConnection, PacketInBoundHandler<T> packet) {
        Value<Result<T>> value = new Value<>();
        UUID uuid = UUID.randomUUID();

        nettyConnection.sendPacket(packet.uuid(uuid), packetState -> {
            if (packetState == PacketState.FAILED) {
                Result<T> r = new Result<>(uuid, null);
                r.setThrowable(new IllegalAccessError("Could not create Query for " + packet.getClass().getSimpleName() + " because PacketState returned " + PacketState.FAILED.name() + "!"));
                value.setValue(r);
                Thread.currentThread().interrupt();
            }
        });
        nettyConnection.getPacketAdapter().registerAdapter(new PacketHandlerAdapter() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof PacketInBoundHandler) {
                    PacketInBoundHandler<T> packetInBoundHandler = (PacketInBoundHandler<T>)packet;
                    if (uuid.equals(packetInBoundHandler.getUniqueId())) {
                        value.setValue(packetInBoundHandler.getResult());
                        nettyConnection.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            }
        });
        int count = 0;

        while (value.getValue() == null && count++ < 3000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        if (count >= 2999) {
            Result<T> r = new Result<>(uuid, null);
            r.setThrowable(new TimeoutException("Request timed out!"));
            value.setValue(r);
        }
        return value.getValue();
    }

    /**
     * Returns the Instance for the
     * {@link MessageAPI} to access
     * all non-static Methods and Fields
     *
     * @return current MessageAPI
     */
    public static MessageAPI getInstance() {
        if (instance == null) {
            instance = new MessageAPI();
        }
        return instance;
    }

}
