
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.util.Cache;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sent by the client in order to enusre that the
 * connection
 * remains active.
 */
@Immutable
public final class PlayInKeepAlive extends PacketIn {
    /**
     * The keep alive time cache
     */
    private static final Cache<NetClient, Integer> TICK_IDS =
            new Cache<>(NetClient.KEEP_ALIVE_KICK_NANOS / 1000000, (client, id) -> {
                client.disconnect("No KeepAlive response");
                return true;
            });

    /**
     * Obtains the next keep alive ID for the given net
     * client
     *
     * @param client the client
     * @return the next teleport ID
     */
    public static int query(NetClient client) {
        return TICK_IDS.compute(client, (k, v) -> {
            if (v == null) {
                // retarded int limit on VarInt, idk
                return ThreadLocalRandom.current().nextInt(0xFFFFFFF);
            } else {
                client.disconnect("No KeepAlive response");
                return null;
            }
        });
    }

    public PlayInKeepAlive() {
        super(PlayInKeepAlive.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int id = NetData.rvint(buf);
        Integer localId = TICK_IDS.get(client);

        if (localId != null && id != localId) {
            client.disconnect("Keep alive ID mismatch, actual:" + localId + " rcvd:" + id);
            return;
        }

        if (System.nanoTime() - client.lastKeepAlive() > NetClient.KEEP_ALIVE_KICK_NANOS) {
            client.disconnect("Timed out");
            return;
        }

        TICK_IDS.compute(client, (k, v) -> null);
    }
}