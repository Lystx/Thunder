
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.entity.living.Player;

/**
 * @author Nick Robson
 */
public class PlayInPlayerAbilities extends PacketIn {

    public PlayInPlayerAbilities() {
        super(PlayInPlayerAbilities.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        byte flags = buf.readByte();

        boolean isGod = (flags & 0x08) != 0;
        boolean canFly = (flags & 0x04) != 0;
        boolean isFlying = (flags & 0x02) != 0;
        boolean isCreative = (flags & 0x01) != 0;

        float flyingSpeed = buf.readFloat();
        float walkingSpeed = buf.readFloat();

        // NOTE: We have to be very careful here, since a hacked client can easily send these things.

        VeraPlayer player = client.getPlayer();
        player.setSprinting(Double.compare(walkingSpeed, Player.DEFAULT_SPRINT_SPEED) == 0);

        if (player.canFly()) {
            player.setFlyingSpeed(flyingSpeed);
            player.setFlying(isFlying, false);
        } else {
            player.setFlying(false, false);
        }

        client.sendPacket(new PlayOutPlayerAbilities(player));
    }
}
