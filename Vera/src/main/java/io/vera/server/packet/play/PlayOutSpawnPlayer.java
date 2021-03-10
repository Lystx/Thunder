
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.server.entity.meta.VeraEntityMeta;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.entity.living.Player;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutSpawnPlayer extends PacketOut {

    private final Player player;

    public PlayOutSpawnPlayer(Player player) {
        super(PlayOutSpawnPlayer.class);
        this.player = player;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.player.getId());

        buf.writeLong(this.player.getUuid().getMostSignificantBits());
        buf.writeLong(this.player.getUuid().getLeastSignificantBits());

        Position pos = this.player.getPosition();
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());

        buf.writeByte(NetData.convertAngle(pos.getYaw()));
        buf.writeByte(NetData.convertAngle(pos.getPitch()));

        ((VeraEntityMeta) this.player.getMetadata()).getMetadata().write(buf);
    }

}
