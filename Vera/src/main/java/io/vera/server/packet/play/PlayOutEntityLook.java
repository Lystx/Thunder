
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.entity.Entity;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutEntityLook extends PacketOut {

    private final int id;
    private final float yaw;
    private final float pitch;
    private final boolean onGround;

    public PlayOutEntityLook(Entity entity) {
        super(PlayOutEntityLook.class);
        this.id = entity.getId();
        this.yaw = entity.getPosition().getYaw();
        this.pitch = entity.getPosition().getPitch();
        this.onGround = entity.isOnGround();
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.id);

        buf.writeByte(NetData.convertAngle(this.yaw));
        buf.writeByte(NetData.convertAngle(this.pitch));

        buf.writeBoolean(this.onGround);
    }

}
