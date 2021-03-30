package org.gravel.library;

import io.vson.annotation.other.Vson;
import lombok.Getter;
import lombok.Setter;
import org.gravel.library.manager.account.AccountManager;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatManager;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.packets.out.PacketOutGlobalInfo;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserManager;
import org.gravel.library.manager.networking.connection.adapter.PacketHandlerAdapter;
import org.gravel.library.manager.networking.connection.packet.Packet;
import org.gravel.library.manager.networking.connection.packet.PacketState;
import org.gravel.library.manager.networking.netty.NettyConnection;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.Result;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.PasswordHasher;
import org.gravel.library.utils.Value;
import org.gravel.library.vson.GravelVsonAdapterChat;
import org.gravel.library.vson.GravelVsonAdapterChatMessage;
import org.gravel.library.vson.GravelVsonAdapterGravelUser;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Getter @Setter
public class GravelAPI {

    private final PasswordHasher passwordHasher;
    private final NettyConnection nettyConnection;

    private AccountManager accountManager;
    private UserManager userManager;
    private ChatManager chatManager;


    private Chat currentChat;

    @Getter
    private static GravelAPI instance;

    private GravelAPI(NettyConnection nettyConnection) {
        instance = this;

        this.nettyConnection = nettyConnection;
        this.passwordHasher = new PasswordHasher();

        Vson.get().registerAdapter(new GravelVsonAdapterGravelUser());
        Vson.get().registerAdapter(new GravelVsonAdapterChatMessage(this));
        Vson.get().registerAdapter(new GravelVsonAdapterChat());
    }

    public void updateChat(Chat chat, JLabel pane, JTextPane outPut, GravelUser client) {
        this.currentChat = chat;
        pane.setText(chat == null ? "You are not in any chat at the momen!" : chat.getName());

        StringBuilder sb = new StringBuilder();
        if (chat == null) {
            List<GravelUser> list = client.getFriends();
            list.removeIf(gravelUser -> gravelUser.getStatus().equals(UserStatus.OFFLINE));

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
            sb.append("[Info] At the moment there are " + list.size() + " of your friends online!").append("\n");
            sb.append("[Info] You got " + client.getRequests().size() + " Friend-Requests!").append("\n");
        } else {
            for (ChatMessage message : chat.getMessages()) {
                sb.append(message.format()).append("\n");
            }
        }
        outPut.setText(sb.toString());
    }

    public static void init(NettyConnection nettyConnection) {
        new GravelAPI(nettyConnection);
    }

    public void sendPacket(Packet packet) {
        this.nettyConnection.sendPacket(packet);
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
        this.sendPacket(new PacketOutGlobalInfo(this.userManager.getUsers()));
    }

    public <T> Result<T> sendQuery(PacketInBoundHandler<T> packet) {
        final Value<Result<T>> value = new Value<>();
        final UUID uuid = UUID.randomUUID();
        nettyConnection.sendPacket(packet.uuid(uuid), packetState -> {
            if (packetState == PacketState.FAILED) {
                Result<T> r = new Result<>(uuid, null);
                r.setThrowable(new IllegalAccessError("Could not create Query for " + packet.getClass().getSimpleName() + " because PacketState returned " + PacketState.FAILED.name() + "!"));
                value.setValue(r);
                Thread.currentThread().interrupt();
            }
        });
        nettyConnection.getPacketAdapter().registerAdapter(new PacketHandlerAdapter() {
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
