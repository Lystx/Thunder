package de.lystx.messenger.commands;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.command.Command;
import de.lystx.messenger.manager.console.Console;
import de.lystx.messenger.networking.packets.in.*;
import de.lystx.messenger.networking.packets.result.PacketGetRequests;

import java.util.List;

public class FriendsCommand extends Command {

    public FriendsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(Console console, String[] args) {
        final Account account = Client.getInstance().getAccount();
        if (args.length == 0) {
            this.help(console);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<String> friends = account.getFriends();
                if (friends.isEmpty()) {
                    console.sendMessage("ERROR", "§cSadly, you do not have any friends!");
                    console.sendMessage("ERROR", "§cBut you can add friends at any time using §efriends add <name>§c!");
                    return;
                }
                console.sendMessage(" ");
                for (String friend : friends) {
                    console.sendMessage("FRIENDS", "&e" + friend);
                }
                console.sendMessage("§8----------------");
            } else {
                help(console);
            }
        } else if (args.length == 2) {
            String player = args[1];
            if (args[0].equalsIgnoreCase("add")) {
                if (account.isFriendsWith(player)) {
                    console.sendMessage("ERROR", "§cYou are already friends with §e" + player + "§c!");
                    return;
                } else if (account.getName().equalsIgnoreCase(player)) {
                    console.sendMessage("ERROR", "§cSad to see you so down bad my guy...");
                    console.sendMessage("ERROR", "§cGo and search for some real friends!");
                    return;
                } else if (MessageAPI.getInstance().getAccount(player) == null) {
                    console.sendMessage("ERROR", "§cThere is no account registered with the name §e" + player + "§c!");
                    return;
                } else if (MessageAPI.getInstance().sendQuery(Client.getInstance().getConnectionClient(), new PacketGetRequests(MessageAPI.getInstance().getAccount(player))).getResult().contains(account.getName())) {
                    console.sendMessage("ERROR", "§cYou already sent a request to §e" + player + "§c!");
                    return;
                }
                Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendRequest(account, player));
                console.sendMessage("INFO", "§aSuccessfully sent a friend-request to §e" + player + "§a!");
            } else if (args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("requests")) {
                List<String> requests = MessageAPI.getInstance().sendQuery(Client.getInstance().getConnectionClient(), new PacketGetRequests(account)).getResult();
                if (requests.isEmpty()) {
                    console.sendMessage("ERROR", "§cSadly, you do not have any requests!");
                    console.sendMessage("ERROR", "§cCome back later§c!");
                    return;
                }
                console.sendMessage(" ");
                for (String friend : requests) {
                    console.sendMessage("REQUESTS", "&e" + friend);
                }
                console.sendMessage("§8----------------");
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!account.isFriendsWith(player)) {
                    console.sendMessage("ERROR", "§cYou are not friends with §e" + player + "§c!");
                    return;
                } else if (MessageAPI.getInstance().getAccount(player) == null) {
                    console.sendMessage("ERROR", "§cThere is no account registered with the name §e" + player + "§c!");
                    return;
                }
                Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendRemove(account, player));
                console.sendMessage("INFO", "§cYou removed §e" + player + " §cfrom your friend-list!");
            } else if (args[0].equalsIgnoreCase("mute")) {
                final Account playerAccount = MessageAPI.getInstance().getAccount(player);
                if (playerAccount == null) {
                    console.sendMessage("ERROR", "§cThere is no account registered with the name §e" + player + "§c!");
                    return;
                }
                if (account.getMutes().contains(playerAccount.getId() + "")) {
                    console.sendMessage("ERROR", "§cThere player §e" + player + " §cis already muted for you!");
                    return;
                }

                Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendMute(account, playerAccount));
                console.sendMessage("INFO", "§cYou muted §e" + playerAccount.getName() + " §cfor yourself!");
            } else if (args[0].equalsIgnoreCase("unmute")) {
                final Account playerAccount = MessageAPI.getInstance().getAccount(player);
                if (playerAccount == null) {
                    console.sendMessage("ERROR", "§cThere is no account registered with the name §e" + player + "§c!");
                    return;
                }
                if (!account.getMutes().contains(playerAccount.getId() + "")) {
                    console.sendMessage("ERROR", "§cThere player §e" + player + " §cis not muted for you!");
                    return;
                }

                Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendMuteRemove(account, playerAccount));
                console.sendMessage("INFO", "§cYou unmuted §e" + playerAccount.getName() + " §cfor yourself!");
            } else if (args[0].equalsIgnoreCase("accept")) {

                List<String> requests = MessageAPI.getInstance().sendQuery(Client.getInstance().getConnectionClient(), new PacketGetRequests(account)).getResult();
                if (requests.isEmpty()) {
                    console.sendMessage("ERROR", "§cSadly, you do not have any requests!");
                    console.sendMessage("ERROR", "§cCome back later§c!");
                    return;
                }
                if (requests.contains(player)) {
                    Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendAccept(account, player));
                    console.sendMessage("INFO", "&fYou accepted the request of §a" + player + "§8!");
                } else {
                    console.sendMessage("ERROR", "§cYou got no request from §e" + player + "§c!");
                    console.sendMessage("ERROR", "§cUse §efriends list requests §cto see your requests!");
                }
            } else if (args[0].equalsIgnoreCase("deny")) {

                List<String> requests = MessageAPI.getInstance().sendQuery(Client.getInstance().getConnectionClient(), new PacketGetRequests(account)).getResult();
                if (requests.isEmpty()) {
                    console.sendMessage("ERROR", "§cSadly, you do not have any requests!");
                    console.sendMessage("ERROR", "§cCome back later§c!");
                    return;
                }
                if (requests.contains(player)) {
                    Client.getInstance().getConnectionClient().sendPacket(new PacketInFriendDeny(account, player));
                    console.sendMessage("INFO", "&fYou denied the request of §c" + player + "§8!");
                } else {
                    console.sendMessage("ERROR", "§cYou got no request from §e" + player + "§c!");
                    console.sendMessage("ERROR", "§cUse §efriends list requests §cto see your requests!");
                }
            } else {
                help(console);
            }
        }
    }

    @Override
    public void help(Console console) {
        console.sendMessage("");
        console.sendMessage("FRIENDS", "&efriends add <name>");
        console.sendMessage("FRIENDS", "&efriends remove <name>");
        console.sendMessage("FRIENDS", "&efriends accept <name>");
        console.sendMessage("FRIENDS", "&efriends deny <name>");
        console.sendMessage("FRIENDS", "&efriends mute <name>");
        console.sendMessage("FRIENDS", "&efriends unmute <name>");
        console.sendMessage("FRIENDS", "&efriends list ");
        console.sendMessage("FRIENDS", "&efriends list <requests>");
        console.sendMessage("");
    }


}
