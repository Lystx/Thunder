package org.gravel.library.manager.networking.packets.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.user.GravelUser;

@Getter @AllArgsConstructor
public class PacketQueryChangeTheme extends PacketInBoundHandler<Boolean> {


    private final GravelUser gravelUser;

    @Override
    public Boolean handleRead(GravelAPI gravelAPI) {

        boolean darkMode = !((boolean) gravelUser.getSettings().get("darkMode"));
        gravelUser.getSettings().put("darkMode", darkMode);
        gravelAPI.getUserManager().update(gravelUser);
        gravelAPI.reload();
        gravelAPI.sendPacket(new PacketOutUpdatePlayer(gravelUser));
        return darkMode;
    }
}
