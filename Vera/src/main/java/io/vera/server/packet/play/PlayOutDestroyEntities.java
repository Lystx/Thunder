
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.entity.Entity;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public final class PlayOutDestroyEntities extends PacketOut {

    private final List<? extends Entity> entities;

    public PlayOutDestroyEntities(List<? extends Entity> entities) {
        super(PlayOutDestroyEntities.class);
        this.entities = entities;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.entities.size());
        this.entities.forEach(entity -> NetData.wvint(buf, entity.getId()));
    }

}