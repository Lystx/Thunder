package org.gravel.library.manager.networking.packets.result;

import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;

@Getter @AllArgsConstructor

public class PacketQueryFriendRequest extends PacketInBoundHandler<VsonObject> {

    private final GravelUser executor;
    private final GravelUser friendToRequest;

    @Override
    public VsonObject handleRead(GravelAPI gravelAPI) {
        VsonObject vsonObject = new VsonObject();

        if (executor.isFriends(friendToRequest)) {
            vsonObject.append("allow", false);
            vsonObject.append("message", "You are already friends with " + friendToRequest.getAccount().getName() + "!");
        } else if (friendToRequest.hasRequest(executor)) {
            vsonObject.append("allow", false);
            vsonObject.append("message", "You are already sent a request to " + friendToRequest.getAccount().getName() + "!");
        } else {
            vsonObject.append("allow", true);
            vsonObject.append("message", "Sucessfully sent a request to " + friendToRequest.getAccount().getName() + "!");
            friendToRequest.getRequests().add(executor.getAccount());
            gravelAPI.getUserManager().update(friendToRequest);
            gravelAPI.sendPacket(new PacketOutUpdatePlayer(friendToRequest));
            gravelAPI.reload();
        }

        return vsonObject;
    }
}
