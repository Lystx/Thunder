
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;

public class PlayInAnimation extends PacketIn {

    public PlayInAnimation() {
        super(PlayInAnimation.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int animation = NetData.rvint(buf);

        VeraPlayer player = client.getPlayer();
        PlayOutAnimation packet = new PlayOutAnimation(player, animation == 0 ? PlayOutAnimation.AnimationType.SWING_MAIN_ARM : PlayOutAnimation.AnimationType.SWING_OFFHAND);
        RecipientSelector.whoCanSee(player, true, packet);
    }

}
