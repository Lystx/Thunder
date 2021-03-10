
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.player.VeraPlayer;


public class PlayOutAnimation extends PacketOut {

    private final VeraPlayer player;
    private final AnimationType animationType;

    public PlayOutAnimation(VeraPlayer player, AnimationType animationType) {
        super(PlayOutAnimation.class);
        this.player = player;
        this.animationType = animationType;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.player.getId());
        buf.writeByte(this.animationType.ordinal());
    }

    public enum AnimationType {

        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFFHAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT

    }

}
