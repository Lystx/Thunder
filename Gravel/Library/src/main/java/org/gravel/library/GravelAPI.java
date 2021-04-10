package org.gravel.library;

import com.formdev.flatlaf.FlatDarkLaf;
import io.thunder.manager.packet.ThunderPacket;
import io.vson.annotation.other.Vson;
import lombok.Getter;
import lombok.Setter;
import org.gravel.library.manager.account.AccountManager;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatManager;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.packets.out.PacketOutGlobalInfo;
import org.gravel.library.manager.networking.packets.result.PacketQueryGetInfo;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserManager;
import org.gravel.library.manager.networking.connection.adapter.PacketHandlerAdapter;
import org.gravel.library.manager.networking.connection.packet.PacketState;
import org.gravel.library.manager.networking.netty.GravelConnection;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.Result;
import org.gravel.library.utils.PasswordHasher;
import org.gravel.library.utils.Value;
import org.gravel.library.vson.GravelVsonAdapterChat;
import org.gravel.library.vson.GravelVsonAdapterChatMessage;
import org.gravel.library.vson.GravelVsonAdapterGravelUser;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Getter @Setter
public class GravelAPI {

    private final PasswordHasher passwordHasher;
    private final GravelConnection gravelConnection;

    private AccountManager accountManager;
    private UserManager userManager;
    private ChatManager chatManager;

    private Chat currentChat;

    @Getter
    private static GravelAPI instance;

    private GravelAPI(GravelConnection gravelConnection) {
        instance = this;

        this.gravelConnection = gravelConnection;
        this.passwordHasher = new PasswordHasher();

        Vson.get().registerAdapter(new GravelVsonAdapterGravelUser());
        Vson.get().registerAdapter(new GravelVsonAdapterChatMessage(this));
        Vson.get().registerAdapter(new GravelVsonAdapterChat());
    }

    public void sendNotification(String toolTip, String caption, String text) {
        try {
            if (System.getProperty("os.name").startsWith("Win")) {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                TrayIcon trayIcon = new TrayIcon(image, toolTip);
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip(toolTip);
                tray.add(trayIcon);
                trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
            } else {
                Runtime.getRuntime().exec(new String[] { "osascript", "-e", "display notification \"" + text + "\" with title \"" + toolTip + "\" subtitle \"" + caption + "\" sound name \"Crystal\"" });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void updateChat(Chat chat, JLabel jLabel, JTextPane outPut, GravelUser client) {
        this.currentChat = chat;

        StringBuilder sb = new StringBuilder();
        if (chat == null) {

            GravelAPI.getInstance().sendQuery(new PacketQueryGetInfo(client)).onResultSet(vsonObjectResult -> {
                sb.append("-------------------------------------------------------------").append("\n");

                sb.append("\n" +
                        "   _____                     _ \n" +
                        "  / ____|                   | |\n" +
                        " | |  __ _ __ __ ___   _____| |\n" +
                        " | | |_ | '__/ _` \\ \\ / / _ \\ |\n" +
                        " | |__| | | | (_| |\\ V /  __/ |\n" +
                        "  \\_____|_|  \\__,_| \\_/ \\___|_|\n" +
                        "                               \n" +
                        "                               ").append("\n");

                sb.append("-------------------------------------------------------------").append("\n");
                sb.append("[Info] Welcome back " + client.getAccount().getName() + "!").append("\n");
                sb.append("[Info] At the moment there are " + client.getFriends().size() + " of your friends online!").append("\n");
                sb.append("[Info] You got " + client.getRequests().size() + " Friend-Requests!").append("\n");

            });
            jLabel.setText("You are in no chat!");

        } else {
            for (ChatMessage message : chat.getMessages()) {
                if (message.getSender().getAccount().getName().equalsIgnoreCase("Lystx")) {
                    sb.append("                                                              " + message.format()).append("\n");
                } else {
                    sb.append(message.format()).append("\n");
                }
            }
            if (chat.isGroup()) {
                jLabel.setText(chat.getName() + " [" + chat.getUsers().size() + " Users]");
            } else {
                final GravelUser otherMember = chat.getOtherMember(client);
                jLabel.setText(otherMember.getAccount().getName() + " [" + otherMember.getStatus() + "]");
            }
        }
        outPut.setText(sb.toString());
    }

    public static void init(GravelConnection gravelConnection) {
        new GravelAPI(gravelConnection);
        FlatDarkLaf.install();
    }

    public void sendPacket(ThunderPacket packet) {
        this.gravelConnection.sendPacket(packet);
    }

    public void schedule(Runnable runnable, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay * 50L);
    }

    public void reload() {
        this.sendPacket(new PacketOutGlobalInfo(
                this.userManager.getUsers(),
                this.accountManager.getAccounts()
        ));
    }

    public <T> Result<T> sendQuery(PacketInBoundHandler<T> packet) {
        final Value<Result<T>> value = new Value<>();
        final UUID uuid = UUID.randomUUID();
        gravelConnection.getPacketAdapter().registerAdapter(new PacketHandlerAdapter() {
            public void handle(ThunderPacket packet) {
                if (packet instanceof PacketInBoundHandler) {
                    PacketInBoundHandler<T> packetInBoundHandler = (PacketInBoundHandler<T>)packet;
                    if (uuid.equals(packetInBoundHandler.getUuid())) {
                        value.setValue(packetInBoundHandler.getResult());
                        gravelConnection.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            }
        });
        gravelConnection.sendPacket(packet.uuid(uuid));
        int count = 0;
        while (value.getValue() == null && count++ < 3000) {
            try {
                Thread.sleep(0L, 500000);
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

}
