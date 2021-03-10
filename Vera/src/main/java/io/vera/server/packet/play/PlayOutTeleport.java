
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the server whenever a player needs to be moved
 * more than 4 blocks.
 */
@Immutable
public class PlayOutTeleport extends PacketOut {
    private final int eid;
    private final Position position;
    private final boolean onGround;

    public PlayOutTeleport(VeraEntity entity, Position teleport) {
        super(PlayOutTeleport.class);
        this.eid = entity.getId();
        this.position = teleport;
        this.onGround = entity.isOnGround();
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.eid);
        buf.writeDouble(this.position.getX());
        buf.writeDouble(this.position.getY());
        buf.writeDouble(this.position.getZ());
        buf.writeByte(NetData.convertAngle(this.position.getYaw()));
        buf.writeByte(NetData.convertAngle(this.position.getPitch()));
        buf.writeBoolean(this.onGround);
    }
}