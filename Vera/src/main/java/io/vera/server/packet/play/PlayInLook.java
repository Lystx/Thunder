
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the player whenever the player turns or looks
 * up and down without moving.
 */
@Immutable
public final class PlayInLook extends PacketIn {
    public PlayInLook() {
        super(PlayInLook.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        boolean onGround = buf.readBoolean();

        VeraPlayer player = client.getPlayer();
        if (player == null) return;
        player.setPosition(player.getPosition().setYaw(yaw).setPitch(pitch), false);
        player.setOnGround(onGround);
    }
}
