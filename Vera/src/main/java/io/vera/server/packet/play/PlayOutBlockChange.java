
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the server to indicate to the client that the
 * block at the given location has changed to the new
 * value.
 */
@Immutable
public final class PlayOutBlockChange extends PacketOut {
    private final Position block;
    private final int newBlock;

    public PlayOutBlockChange(Position block, int newBlock) {
        super(PlayOutBlockChange.class);
        this.block = block;
        this.newBlock = newBlock;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvec(buf, this.block);
        NetData.wvint(buf, this.newBlock);
    }
}