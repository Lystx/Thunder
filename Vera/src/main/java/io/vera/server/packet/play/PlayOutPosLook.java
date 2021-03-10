
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class PlayOutPosLook extends PacketOut {

    private final VeraPlayer player;
    private final Position pos;

    public PlayOutPosLook(VeraPlayer player) {
        super(PlayOutPosLook.class);
        this.player = player;
        this.pos = player.getPosition();
    }

    public PlayOutPosLook(VeraPlayer player, Position pos) {
        super(PlayOutPosLook.class);
        this.player = player;
        this.pos = pos;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeDouble(this.pos.getX());
        buf.writeDouble(this.pos.getY());
        buf.writeDouble(this.pos.getZ());
        buf.writeFloat(this.pos.getYaw());
        buf.writeFloat(this.pos.getPitch());
        buf.writeByte(0); // all absolute
        NetData.wvint(buf, PlayInTeleportConfirm.query(this.player.net()));
    }
}
