package org.gravel.library.manager.networking.packets.result;

import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.AppendableMap;
import org.gravel.library.utils.PasswordHasher;

import java.util.Date;
import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketQueryLogOut extends PacketInBoundHandler<VsonObject> {

    private final GravelUser gravelUser;

    @Override
    public VsonObject handleRead(GravelAPI gravelAPI) {
        System.out.println("[Server] " + gravelUser.getAccount().getName() + " has disconnected from GravelMessenger!");
        gravelUser.setStatus(UserStatus.OFFLINE);
        GravelAPI.getInstance().getUserManager().update(gravelUser);
        GravelAPI.getInstance().reload();
        return new VsonObject();
    }
}
