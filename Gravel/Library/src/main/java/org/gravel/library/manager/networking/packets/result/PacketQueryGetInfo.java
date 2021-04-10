package org.gravel.library.manager.networking.packets.result;

import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;

import java.util.List;

@Getter @AllArgsConstructor
public class PacketQueryGetInfo extends PacketInBoundHandler<VsonObject> {

    private final GravelUser gravelUser;

    @Override
    public VsonObject handleRead(GravelAPI gravelAPI) {
        VsonObject vsonObject = new VsonObject();
        final GravelUser user = gravelAPI.getUserManager().getUser(this.gravelUser.getAccount().getUniqueId());
        List<Account> list = user.getFriends();

        for (Account account : list) {
            GravelUser gravelUser = gravelAPI.getUserManager().getUser(account.getUniqueId());
            if (gravelUser.getStatus().equals(UserStatus.OFFLINE)) {
                list.remove(account);
            }
        }
        vsonObject.append("friends", list.size());
        vsonObject.append("requests", user.getRequests().size());
        vsonObject.append("muted", user.getMuted().size());
        return vsonObject;
    }
}
