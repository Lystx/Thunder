package de.lystx.messenger;

import de.lystx.messenger.commands.*;
import de.lystx.messenger.handler.ChatHandler;
import de.lystx.messenger.handler.ConnectionHandler;
import de.lystx.messenger.handler.UpdateHandler;
import de.lystx.messenger.manager.FileManager;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.chats.Chat;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.manager.setup.impl.AccountLogin;
import de.lystx.messenger.manager.setup.impl.AccountSetup;
import de.lystx.messenger.networking.connection.adapter.PacketHandlerAdapter;
import de.lystx.messenger.networking.connection.packet.Packet;
import de.lystx.messenger.networking.netty.ConnectionClient;
import de.lystx.messenger.networking.packets.in.PacketInCreateAccount;
import de.lystx.messenger.networking.packets.in.PacketInLogin;
import de.lystx.messenger.networking.packets.out.PacketOutAccountResult;
import de.lystx.messenger.networking.packets.out.PacketOutLogin;
import de.lystx.messenger.networking.packets.result.PacketGetRequests;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Getter
public class Client {

    private final Console console;
    private final ConnectionClient connectionClient;
    private final FileManager fileManager;

    @Setter
    private Account account;

    @Getter
    private static Client instance;


    private List<Chat> chats;

    public Client() {
        instance = this;

        this.fileManager = new FileManager();

        this.console = new Console("&f[&eUnknown&f] &f");
        this.connectionClient = new ConnectionClient(
                (MessageAPI.getInstance().getHost() != null ? MessageAPI.getInstance().getHost() : "127.0.0.1"),
                (MessageAPI.getInstance().getPort() != null ? MessageAPI.getInstance().getPort() : 1758)
        );
        this.connectionClient.onError(e -> {
            this.console.sendMessage("ERROR", "§cCouldn't connect to §eMessenger§c!");
            this.console.sendMessage("ERROR", "§cShutting down System§c....");
            System.exit(187);
        });
        this.connectionClient.start();

        MessageAPI.getInstance().setNettyConnection(this.connectionClient);

        this.connectionClient.getPacketAdapter().registerAdapter(new ConnectionHandler());
        this.connectionClient.getPacketAdapter().registerAdapter(new UpdateHandler());
        this.connectionClient.getPacketAdapter().registerAdapter(new ChatHandler());

        MessageAPI.getInstance().getCommandManager().registerCommand(new NavigateCommand("-s", "Navigates into a chat", "-nav", "-navigate"));
        MessageAPI.getInstance().getCommandManager().registerCommand(new FriendsCommand("friends", "Manages your Friends", "friend"));
        MessageAPI.getInstance().getCommandManager().registerCommand(new LeaveCommand("-l", "Leaves the current chat", "-leave"));
        MessageAPI.getInstance().getCommandManager().registerCommand(new GroupCommand("group", "Creates a group", "-g"));
        MessageAPI.getInstance().getCommandManager().registerCommand(new HubCommand("hub", "Goes to main menu", "-h"));

        this.console.sendMessage("\n" +
                "   _____ _ _            _   \n" +
                "  / ____| (_)          | |  \n" +
                " | |    | |_  ___ _ __ | |_ \n" +
                " | |    | | |/ _ \\ '_ \\| __|\n" +
                " | |____| | |  __/ | | | |_ \n" +
                "  \\_____|_|_|\\___|_| |_|\\__|\n" +
                "                            \n" +
                "                            ");
        this.console.sendMessage("&8--------------------------------");
        this.console.sendMessage("NETWORK", "&fSetting up &eComponents&8...");
        this.console.sendMessage("NETWORK", "&fLoading &eSettings&8...");
        this.console.sendMessage("NETWORK", "&fRequesting &eLog-In&8...");
        this.console.sendMessage("NETWORK", "&fConnecting to &e" + this.connectionClient.getHost() + "&8:&e" + this.connectionClient.getPort() + "&8...");

        MessageAPI.getInstance().init(this.console);
    }

    @SneakyThrows
    public void onConnect() {
        if (!this.fileManager.getConfig().getBoolean("registered")) {
            new AccountSetup().start(this.console, accountSetup -> {
                try {
                    this.connectionClient.sendPacket(new PacketInCreateAccount(accountSetup.getName().trim(), accountSetup.getPassword().trim(), accountSetup.getReEnteredPassword().trim(), InetAddress.getLocalHost().getHostAddress()));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            });
            this.connectionClient.getPacketAdapter().registerAdapter(new PacketHandlerAdapter() {
                @Override
                public void handle(Packet packet) {
                    if (packet instanceof PacketOutAccountResult) {
                        if (account == null) {
                            PacketOutAccountResult packetOutAccountResult = (PacketOutAccountResult) packet;
                            console.sendMessage("INFO", packetOutAccountResult.getVsonObject().getString("message"));
                            if (packetOutAccountResult.getVsonObject().getBoolean("allow")) {
                                final VsonObject config = fileManager.getConfig();
                                config.append("registered", true).save();
                                console.sendMessage("INFO", "&aSetup done&8! &cStopping Messenger....");
                                System.exit(1);
                            } else {
                                onConnect();
                            }
                        }
                    }
                }
            });
        } else {
            this.console.sendMessage("&8------------------------------");

            new AccountLogin().start(this.console, accountLogin -> {
                String user = accountLogin.getUserName();
                String pw = accountLogin.getPassword();
                this.connectionClient.sendPacket(new PacketInLogin(user.trim(), pw.trim()));
                this.connectionClient.getPacketAdapter().registerAdapter(new PacketHandlerAdapter() {
                    @Override
                    public void handle(Packet packet) {
                        if (packet instanceof PacketOutLogin) {
                            if (account == null) {
                                PacketOutLogin packetOutLogin = (PacketOutLogin) packet;
                                if (packetOutLogin.isAllow()) {
                                    account = packetOutLogin.getAccount();
                                    MessageAPI.getInstance().setAccount(account);
                                    mainMenu(packetOutLogin.getAccounts(), packetOutLogin.getChats());
                                } else {
                                    console.sendMessage("INFO", "&cWrong &eUsername &cor &epassword&c!");
                                    onConnect();
                                }
                            }
                        }
                    }
                });

            });

        }
    }

    public void mainMenu(List<Account> accounts, List<Chat> chats) {

        this.chats = chats;
        MessageAPI.getInstance().setChats(chats);


        console.clearScreen();
        console.sendMessage("INFO", "&aWelcome back &e" + account.getName() + "&8!");

        MessageAPI.getInstance().getAccounts().addAll(accounts);

        if (!chats.isEmpty()) {
            for (Chat chat : this.account.getChats()) {
                console.sendMessage("CHATS", "&8(#&f" + chat.getId() + "&8) &e" + chat.getName() + " &8| &fMembers: &a" + chat.getAccounts().size() + " &8| &fMessages &a" + chat.getMessages().size());
            }
        } else {
            console.sendMessage("CHATS", "§cYou are not in any chats! Try to add somebody as &efriend&c! Socially interact with other people!");
        }

        final List<String> result = MessageAPI.getInstance().sendQuery(connectionClient, new PacketGetRequests(account)).getResult();
        if (!result.isEmpty()) {
            console.sendMessage("REQUESTS", "§fYou still got §e" + result.size() + " §frequests open§8!");
            console.sendMessage("REQUESTS", "§fView them by typing §efriends list requests§f!");
        } else {
            console.sendMessage("REQUESTS", "§fYou do not have any §frequests open§8!");
        }
        console.sendMessage("&8--------------------");
    }

    public void shutdown() {
        this.console.stop();
        this.connectionClient.disconnect();
    }
}
