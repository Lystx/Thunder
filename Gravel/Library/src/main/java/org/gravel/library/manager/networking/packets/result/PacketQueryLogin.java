package org.gravel.library.manager.networking.packets.result;

import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutNotify;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.AppendableMap;
import org.gravel.library.utils.PasswordHasher;
import org.gravel.library.utils.Utils;

import javax.swing.*;
import java.util.*;

@Getter @AllArgsConstructor
public class PacketQueryLogin extends PacketInBoundHandler<VsonObject> {

    private final String name;
    private final String password1;
    private final String password2;
    private final String ip;


    @Override
    public VsonObject handleRead(GravelAPI gravelAPI) {
        PasswordHasher passwordHasher = gravelAPI.getPasswordHasher();
        VsonObject vsonObject = new VsonObject(VsonSettings.OVERRITE_VALUES);
        Account account = gravelAPI.getAccountManager().getAccount(this.name);
        if (password2 == null) {
            if (account == null) {
                vsonObject.append("allow", false);
                vsonObject.append("message", "There is no existing account with the name " + name + "!");
            } else {
                boolean b = passwordHasher.authenticate(password1, account.getPassword());
                vsonObject.append("allow", b);
                if (b) {
                    final GravelUser user = gravelAPI.getUserManager().getUser(account.getUniqueId());
                    if (!user.getStatus().equals(UserStatus.OFFLINE)) {
                        vsonObject.append("allow", false);
                        vsonObject.append("message", "You are already logged in with that account!");
                        System.out.println("[Client] " + account.getName() + " tried to login from " + this.ip + " but " + account.getName() + " is already logged in!");
                        GravelAPI.getInstance().sendPacket(new PacketOutNotify(user, "Somebody tried to log in to your Account!", "Gravel | Account Security", JOptionPane.INFORMATION_MESSAGE));
                    } else {
                        vsonObject.append("message", "Login worked!");
                        vsonObject.append("user", new VsonObject()
                                .append("account", user.getAccount())
                                .append("settings", user.getSettings())
                                .append("friends", Utils.fromList(user.getFriends()))
                                .append("muted", Utils.fromList(user.getMuted()))
                                .append("requests", Utils.fromList(user.getRequests()))
                                .append("status", user.getStatus().name())
                        );
                        user.setStatus(UserStatus.ONLINE);
                        gravelAPI.getUserManager().update(user);
                        GravelAPI.getInstance().sendPacket(new PacketOutUpdatePlayer(user));
                        System.out.println("[Client] " + account.getName() + " logged in successfully from " + this.ip + "!");
                    }
                } else {
                    vsonObject.append("message", "Wrong password provided for Account " + account.getName());
                    System.out.println("[Client] " + this.ip + " provided wrong password for account with name " + account.getName() + "!");
                }
            }
        } else {

            if (account != null) {
                vsonObject.append("allow", false);
                vsonObject.append("message", "There is already an existing account with the name " + name + "!");
            } else if (!password1.equals(password2)) {
                vsonObject.append("allow", false);
                vsonObject.append("message", "Passwords do not match! Retry!");
            } else {
                gravelAPI.getUserManager().addUser(new GravelUser(
                        new Account(
                                UUID.randomUUID(),
                                this.name,
                                passwordHasher.hash(this.password1),
                                this.ip,
                                new Date().getTime()
                        ), new AppendableMap<String, Object>().append("darkMode", true),
                        new LinkedList<>(),
                        new LinkedList<>(),
                        new LinkedList<>(),
                        UserStatus.OFFLINE
                ));
                vsonObject.append("allow", true);
                vsonObject.append("message", "Account created!");
            }
        }
        GravelAPI.getInstance().reload();
        return vsonObject;
    }
}
