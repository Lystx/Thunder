package io.vera.server.player;

import io.vera.entity.Entity;
import io.vera.server.entity.VeraEntity;
import io.vera.server.packet.PacketOut;
import io.vera.server.world.Chunk;
import io.vera.server.world.World;
import io.vera.world.other.Position;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RecipientSelector {

    public static void whoCanSee(Chunk chunk, Entity exclude, PacketOut... packetOut) {
        if (chunk == null)
            throw new IllegalStateException("Player cannot inhabit an unloaded chunk");
        Set<VeraPlayer> targets = chunk.getHolders();
        if (!(exclude instanceof io.vera.entity.living.Player)) {
            for (VeraPlayer p : targets) {
                for (PacketOut out : packetOut)
                    p.net().sendPacket(out);
            }
        } else {
            for (VeraPlayer p : targets) {
                if (p.equals(exclude))
                    continue;
                for (PacketOut out : packetOut)
                    p.net().sendPacket(out);
            }
        }
    }

    public static void whoCanSee(VeraEntity canSee, boolean exclude, PacketOut... packetOut) {
        Position pos = canSee.getPosition();
        whoCanSee(canSee.getWorld().getChunkAt(pos.getChunkX(), pos.getChunkZ(), true), exclude ? (Entity)canSee : null, packetOut);
    }

    public static void inWorld(World world, PacketOut... packetOut) {
        for (VeraPlayer player : world.getOccupants()) {
            for (PacketOut out : packetOut)
                player.net().sendPacket(out);
        }
    }
}
