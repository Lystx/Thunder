
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.entity.Entity;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutEntityRelativeMove extends PacketOut {

    private final Entity entity;
    private final Position delta;

    public PlayOutEntityRelativeMove(Entity entity, Position delta) {
        super(PlayOutEntityRelativeMove.class);
        this.entity = entity;
        this.delta = delta.multiply(4096, 4096, 4096);
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.entity.getId());

        buf.writeShort((short) this.delta.getX());
        buf.writeShort((short) this.delta.getY());
        buf.writeShort((short) this.delta.getZ());

        buf.writeBoolean(this.entity.isOnGround());
    }

}
