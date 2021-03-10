
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Vector;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * This packet is sent to indicate to the client where
 * their
 * bed/compass will point towards.
 */
@Immutable
public final class PlayOutSpawnPos extends PacketOut {
    /**
     * The default spawn position
     */
    public static final Vector VEC = new Vector();

    public PlayOutSpawnPos() {
        super(PlayOutSpawnPos.class);
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvec(buf, VEC);
    }
}