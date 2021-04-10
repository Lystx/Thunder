package org.gravel.library.manager.networking.packets.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.user.GravelUser;

@Getter @AllArgsConstructor
public class PacketQueryMutePlayer extends PacketInBoundHandler<Boolean> {

    private final GravelUser executor;
    private final GravelUser toBeMuted;


    @Override
    public Boolean handleRead(GravelAPI gravelAPI) {
        boolean ret = !executor.hasMuted(toBeMuted);
        if (ret) {
            executor.getMuted().add(this.toBeMuted.getAccount());
        } else {
            executor.getMuted().removeIf(user -> user.getName().equalsIgnoreCase(this.toBeMuted.getAccount().getName()));
        }
        gravelAPI.getUserManager().update(executor);
        gravelAPI.reload();
        return ret;
    }
}
