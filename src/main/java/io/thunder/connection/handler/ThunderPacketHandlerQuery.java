package io.thunder.connection.handler;

import io.thunder.connection.ThunderConnection;
import io.thunder.manager.packet.ThunderPacket;
import io.thunder.manager.packet.featured.Query;
import io.thunder.manager.packet.featured.QueryPacket;
import io.thunder.manager.packet.handler.PacketHandler;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This {@link PacketHandler} is used to handle the {@link QueryPacket}
 * It will handle the Packet and set the {@link Query} with a Result in it
 * Then the Packet will be send back to the sender and there it will handle
 * the rest for you
 *
 */
@Getter @AllArgsConstructor
public class ThunderPacketHandlerQuery implements PacketHandler {

    private final ThunderConnection thunderConnection;

    @Override
    public void handle(ThunderPacket packet) {
        if (packet instanceof QueryPacket) {
            QueryPacket queryPacket = (QueryPacket)packet;
            VsonObject jsonObject = new VsonObject();

            queryPacket.handleQuery(jsonObject);

            Query query = new Query(jsonObject);
            queryPacket.setQuery(query);

            if (!queryPacket.isSendBack()) {
                return;
            }
            queryPacket.setSendBack(false);
            this.thunderConnection.sendPacket(queryPacket);
        }
    }
}
