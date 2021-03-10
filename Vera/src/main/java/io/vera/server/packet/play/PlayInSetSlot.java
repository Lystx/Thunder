
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the client to indicate to the server that the
 * player has selected a new slot.
 */
@Immutable
public class PlayInSetSlot extends PacketIn {
    public PlayInSetSlot() {
        super(PlayInSetSlot.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        client.getPlayer().getInventory().setSelectedSlot(buf.readShort());
    }
}