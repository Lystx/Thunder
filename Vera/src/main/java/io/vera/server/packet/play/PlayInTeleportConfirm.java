
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.util.Cache;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Teleportation confirm packet. Sent after packets such as
 * {@link PlayOutPosLook} to confirm the ID.
 */
@ThreadSafe
public final class PlayInTeleportConfirm extends PacketIn {
    /**
     * The teleport ID cache
     */
    private static final Cache<NetClient, IdBlock> TELEPORT_ID =
            new Cache<>(NetClient.KEEP_ALIVE_KICK_NANOS / 1000000, (client, id) -> {
                int block = id.getBlockSize();
                int cur = id.getCount();

                if (cur < block) {
                    client.disconnect("No teleport response");
                }

                return cur == block;
            });

    /**
     * A block of reserved IDs for a particular client
     */
    private static class IdBlock {
        /**
         * The ID counter for the max reserved block
         */
        private volatile int counter;
        /**
         * The IDs that have been checked in
         */
        private volatile int checkedIn;

        // Field updaters to save memory
        private static final AtomicIntegerFieldUpdater<IdBlock> COUNTER =
                AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "counter");
        private static final AtomicIntegerFieldUpdater<IdBlock> CHECK_IN =
                AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "checkedIn");

        /**
         * Obtains the next ID value in this block.
         *
         * @return the next ID
         */
        int checkOut() {
            return COUNTER.getAndAdd(this, 1);
        }

        /**
         * Invalidates the block portion that has been
         * already reserved when a confirmation packet is
         * received.
         *
         * @param id the id to invalidate
         * @return {@code true} if the ID is good,
         * {@code false} if it has not been reserved
         */
        boolean checkIn(int id) {
            boolean good = COUNTER.get(this) >= id;
            if (good) {
                CHECK_IN.addAndGet(this, 1);
            }

            return good;
        }

        /**
         * Obtains the max block size for this set of IDs.
         *
         * @return the max block size
         */
        int getBlockSize() {
            return COUNTER.get(this);
        }

        /**
         * Obtains the current block count for this set of
         * ID values.
         *
         * @return the current checked in IDs
         */
        int getCount() {
            return CHECK_IN.get(this);
        }
    }

    /**
     * Obtains the next teleport ID for the given net
     * client
     *
     * @param client the client
     * @return the next teleport ID
     */
    public static int query(NetClient client) {
        IdBlock block = TELEPORT_ID.get(client, IdBlock::new);
        return block.checkOut();
    }

    public PlayInTeleportConfirm() {
        super(PlayInTeleportConfirm.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int id = NetData.rvint(buf);
        IdBlock block = TELEPORT_ID.get(client);

        if (block != null) {
            if (block.checkIn(id)) {
                client.getPlayer().resumeLogin();
            } else {
                client.disconnect("Mismatched confirmation ID (" + id + ')');
            }
        }
    }
}