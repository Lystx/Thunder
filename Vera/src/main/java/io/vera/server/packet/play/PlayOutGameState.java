
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the server to the client whenever the indicated
 * game state should change, such as for an invalid bed,
 * raining, or game mode toggles.
 */
@Immutable
public final class PlayOutGameState extends PacketOut {
    private final int reason;
    private final float val;

    public PlayOutGameState(int reason, float val) {
        super(PlayOutGameState.class);
        this.reason = reason;
        this.val = val;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.reason);
        buf.writeFloat(this.val);
    }
}
