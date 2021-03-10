
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Players update the server time on the client. This packet
 * should only be used to resync the world time with the
 * client time.
 */
@Immutable
public final class PlayOutTime extends PacketOut {
    private final long age;
    private final long currentTime;

    public PlayOutTime(long age, long currentTime) {
        super(PlayOutTime.class);
        this.age = age;
        this.currentTime = currentTime;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.age);
        buf.writeLong(this.currentTime);
    }
}