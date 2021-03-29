package de.lystx.messenger.manager;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.account.AccountManager;
import de.lystx.messenger.networking.netty.NettyConnection;
import de.lystx.messenger.networking.packets.out.PacketOutUpdateAccount;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class FriendManager {

    private final VsonObject vsonObject;
    private final NettyConnection connection;

    @SneakyThrows
    public FriendManager(File directory, NettyConnection connection) {
        this.vsonObject = new VsonObject(new File(directory, "friends.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        this.connection = connection;
        this.vsonObject.save();
    }

    public void addRequest(Account account, String requester) {
        final List<String> requests = this.getRequests(account);

        requests.add(requester);
        this.vsonObject.append(account.getId() + "", requests);
        this.vsonObject.save();

        this.connection.sendPacket(new PacketOutUpdateAccount(account));
    }

    public void removeRequest(Account account, String requester) {
        final List<String> requests = this.getRequests(account);

        requests.remove(requester);
        this.vsonObject.append(account.getId() + "", requests);
        this.vsonObject.save();
        this.connection.sendPacket(new PacketOutUpdateAccount(account));
    }

    public void addFriends(Account account, String friends) {
        final AccountManager accountManager = MessageAPI.getInstance().getAccountManager();
        account.getFriends().add(friends);
        accountManager.update(account.getId() + "", account);
        this.connection.sendPacket(new PacketOutUpdateAccount(account));
    }

    public void removeFriends(Account account, String friends) {
        final AccountManager accountManager = MessageAPI.getInstance().getAccountManager();
        account.getFriends().remove(friends);
        accountManager.update(account.getId() + "", account);
        this.connection.sendPacket(new PacketOutUpdateAccount(account));
    }

    public List<String> getRequests(Account account) {
        if (this.vsonObject == null) {
            return new LinkedList<>();
        }
        if (!this.vsonObject.has(String.valueOf(account.getId()))) {
            this.vsonObject.append(String.valueOf(account.getId()), new LinkedList<>());
            this.vsonObject.save();
            return new LinkedList<>();
        } else {
            return this.vsonObject.getList(String.valueOf(account.getId()), String.class);
        }
    }
}
