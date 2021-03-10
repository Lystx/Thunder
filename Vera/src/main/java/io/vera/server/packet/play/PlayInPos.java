
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;

/**
 * Packet received by the server upon the client requesting
 * the player entity to move.
 */
@Immutable
public final class PlayInPos extends PacketIn {
    public PlayInPos() {
        super(PlayInPos.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        double x = buf.readDouble();
        double feetY = buf.readDouble();
        double z = buf.readDouble();
        boolean onGround = buf.readBoolean();

        VeraPlayer player = client.getPlayer();
        player.setPosition(player.getPosition().set(x, feetY, z), false);
        player.setOnGround(onGround);
    }
}
