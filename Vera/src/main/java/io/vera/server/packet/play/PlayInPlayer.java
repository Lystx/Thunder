
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the client upon joining the server in order to
 * update the player.
 */
@Immutable
public final class PlayInPlayer extends PacketIn {
    public PlayInPlayer() {
        super(PlayInPlayer.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        boolean onGround = buf.readBoolean();
        client.getPlayer().setOnGround(onGround);
    }
}
