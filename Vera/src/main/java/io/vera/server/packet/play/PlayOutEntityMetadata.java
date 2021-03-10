
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.entity.VeraEntity;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;


public class PlayOutEntityMetadata extends PacketOut {

    private final VeraEntity entity;

    public PlayOutEntityMetadata(VeraEntity entity) {
        super(PlayOutEntityMetadata.class);
        this.entity = entity;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.entity.getId());
        this.entity.getMetadata().getMetadata().write(buf);
    }

}
