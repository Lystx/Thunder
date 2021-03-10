
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import io.vera.server.world.Chunk;

import javax.annotation.concurrent.Immutable;

/**
 * Sends the client the data contained by the chunk.
 */
@Immutable
public final class PlayOutChunk extends PacketOut {
    private final Chunk chunk;

    public PlayOutChunk(Chunk chunk) {
        super(PlayOutChunk.class);
        this.chunk = chunk;
    }

    @Override
    public void write(ByteBuf buf) {
        boolean doGUContinuous = true;

        buf.writeInt(this.chunk.getX());
        buf.writeInt(this.chunk.getZ());
        buf.writeBoolean(doGUContinuous);
        this.chunk.write(buf, doGUContinuous);
    }
}
