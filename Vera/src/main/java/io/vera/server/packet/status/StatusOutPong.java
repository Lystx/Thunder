
package io.vera.server.packet.status;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;


public final class StatusOutPong extends PacketOut {

    private final long time;

    public StatusOutPong(long time) {
        super(StatusOutPong.class);
        this.time = time;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.time);
    }
}