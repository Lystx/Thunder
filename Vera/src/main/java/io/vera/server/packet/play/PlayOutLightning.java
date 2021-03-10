
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.vector.AbstractVector;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the server to spawn a lightning packet for a
 * particular player.
 */
@Immutable
public final class PlayOutLightning extends PacketOut {
    private final double x;
    private final double y;
    private final double z;

    public PlayOutLightning(AbstractVector<?> vector) {
        super(PlayOutLightning.class);
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, VeraEntity.EID_COUNTER.incrementAndGet());
        buf.writeByte(1); // apparently the only useful value
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }
}