
package io.vera.server.entity;

import io.netty.channel.ChannelFutureListener;
import io.vera.server.packet.play.*;
import lombok.Getter;
import lombok.Setter;
import io.vera.world.other.Position;
import io.vera.entity.Entity;
import io.vera.entity.living.Player;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.entity.meta.EntityMetaType;
import io.vera.server.entity.meta.VeraEntityMeta;
import io.vera.server.net.EntityMetadata;
import io.vera.server.packet.PacketOut;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.Chunk;
import io.vera.server.world.World;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@ThreadSafe
public abstract class VeraEntity implements Entity {

    public static final AtomicInteger EID_COUNTER = new AtomicInteger();

    protected final ServerThreadPool pool;
    private final Runnable tickingTask = this::doTick;
    @Getter
    private final int id;
    @GuardedBy("pool")
    private Position position;
    @Getter
    @Setter
    private volatile boolean onGround;
    @Getter
    private final VeraEntityMeta metadata;

    public VeraEntity(World world, PoolSpec spec) {
        this.id = EID_COUNTER.incrementAndGet();
        this.pool = ServerThreadPool.forSpec(spec);

        Position pos = world.getWorldOptions().getSpawn().toPosition(world);
        this.position = pos;

        if (this instanceof Player) {
            VeraPlayer player = (VeraPlayer) this;
            world.getOccupants().add(player);
            world.getChunkAt(pos.getChunkX(), pos.getChunkZ()).getOccupants().add(player);
        } else {
            world.getEntitySet().add(this);
        }

        EntityMetaType metaType = this.getClass().getAnnotation(EntityMetaType.class);
        if (metaType == null) {
            throw new RuntimeException(this.getClass() + " doesn't have an EntityMetaType annotation!");
        }

        try {
            this.metadata = metaType.value().getConstructor(EntityMetadata.class).newInstance(new EntityMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Position getPosition() {
        synchronized (this.pool) {
            return this.position;
        }
    }

    @Override
    public final void setPosition(Position position) {
        this.setPosition(position, true);
    }

    public void setPosition(Position position, boolean sendUpdate) {
        synchronized (this.pool) {
            Position old = this.position;

            World fromWorld = old.getWorld();
            World destWorld = position.getWorld();
            if (!destWorld.equals(fromWorld)) {
                if (this instanceof Player) {
                    fromWorld.getOccupants().remove(this);
                    destWorld.getOccupants().add((VeraPlayer) this);
                } else {
                    fromWorld.getEntitySet().remove(this);
                    destWorld.getEntitySet().add(this);
                }
            }

            int destCX = position.getChunkX();
            int destCZ = position.getChunkZ();
            Chunk destChunk = destWorld.getChunkAt(destCX, destCZ);
            int fromCX = old.getChunkX();
            int fromCZ = old.getChunkZ();
            if (fromCX != destCX || fromCZ != destCZ) {
                Chunk fromChunk = fromWorld.getChunkAt(fromCX, fromCZ, false);
                List<Entity> destroy = Collections.singletonList(this);

                PacketOut spawnThis = this.getSpawnPacket();
                if (this instanceof Player) {
                    VeraPlayer player = (VeraPlayer) this;
                    if (fromChunk != null) {
                        fromChunk.getOccupants().remove(player);
                    }
                    destChunk.getOccupants().add(player);

                    Stream.concat(fromChunk == null ? Stream.empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream()).
                            distinct().
                            forEach(p -> {
                                if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                                    if (p.equals(this)) {
                                        return;
                                    }

                                    p.net().sendPacket(spawnThis);
                                }

                                if (!destChunk.getHolders().contains(p)) {
                                    p.net().sendPacket(new PlayOutDestroyEntities(destroy));
                                }
                            });
                    player.updateChunks(position);
                } else {
                    if (fromChunk != null) {
                        fromChunk.getEntitySet().remove(this);
                    }

                    destChunk.getEntitySet().add(this);

                    Stream.concat(fromChunk == null ? Stream.empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream()).
                            distinct().
                            forEach(p -> {
                                if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                                    if (p.equals(this)) {
                                        return;
                                    }

                                    p.net().sendPacket(spawnThis);
                                }

                                if (!destChunk.getHolders().contains(p)) {
                                    p.net().sendPacket(new PlayOutDestroyEntities(destroy));
                                }
                            });
                }
            }

            Position delta = position.subtract(old);
            if (delta.getX() != 0 || delta.getY() != 0 || delta.getZ() != 0) {
                if (old.distanceSquared(position) > 16) {
                    this.teleport(destChunk, position, sendUpdate);
                } else {
                    if (Double.compare(old.getYaw(), position.getYaw()) == 0 && Double.compare(old.getPitch(), position.getPitch()) == 0) {
                        PlayOutEntityRelativeMove packet = new PlayOutEntityRelativeMove(this, delta);
                        this.updatePosition(destChunk, position, sendUpdate, packet);
                    } else {
                        PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
                        PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
                        this.updatePosition(destChunk, position, sendUpdate, lookAndRelativeMove, look);
                    }
                }
            } else if (Float.compare(old.getYaw(), position.getYaw()) != 0 || Float.compare(old.getPitch(), position.getPitch()) != 0) {
                PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
                PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
                this.updatePosition(destChunk, position, sendUpdate, lookAndRelativeMove, look);
            }
        }
    }

    private void updatePosition(Chunk chunk, Position pos, boolean sendUpdate, PacketOut... packetOut) {
        if (chunk == null) {
            throw new IllegalStateException("Player cannot inhabit an unloaded chunk");
        }

        Set<VeraPlayer> targets = chunk.getHolders();
        for (Iterator<VeraPlayer> it = targets.iterator(); ; ) {
            if (!it.hasNext()) {
                break;
            }

            VeraPlayer p = it.next();
            if (p.equals(this)) {
                if (sendUpdate) {
                    if (!it.hasNext()) {
                        p.net().sendPacket(new PlayOutPosLook(p, pos)).addListener((ChannelFutureListener) future -> {
                            synchronized (this.pool) {
                                this.position = pos;
                            }
                        });
                        return;
                    } else {
                        p.net().sendPacket(new PlayOutPosLook(p, pos));
                    }
                } else {
                    synchronized (this.pool) {
                        this.position = pos;
                    }
                }
                continue;
            }

            for (int i = 0; i < packetOut.length; i++) {
                PacketOut out = packetOut[i];
                if (!it.hasNext() && i == packetOut.length - 1) {
                    p.net().sendPacket(out).addListener((ChannelFutureListener) future -> {
                        synchronized (this.pool) {
                            this.position = pos;
                        }
                    });
                    return;
                } else {
                    p.net().sendPacket(out);
                }
            }
        }
    }

    private void teleport(Chunk chunk, Position pos, boolean sendUpdate) {
        if (chunk == null) {
            throw new IllegalStateException("Player cannot inhabit an unloaded chunk");
        }

        PlayOutTeleport teleport = new PlayOutTeleport(this, pos);
        Set<VeraPlayer> targets = chunk.getHolders();
        if (this instanceof VeraPlayer) {
            targets.add((VeraPlayer) this);
        }

        for (Iterator<VeraPlayer> it = targets.iterator(); ; ) {
            if (!it.hasNext()) {
                break;
            }

            VeraPlayer p = it.next();
            if (p.equals(this)) {
                if (sendUpdate) {
                    if (!it.hasNext()) {
                        p.net().sendPacket(new PlayOutPosLook(p, pos));
                        synchronized (this.pool) {
                            this.position = pos;
                        }
                        return;
                    } else {
                        p.net().sendPacket(new PlayOutPosLook(p, pos));
                    }
                } else {
                    synchronized (this.pool) {
                        this.position = pos;
                    }
                }
                continue;
            }

            if (!it.hasNext()) {
                p.net().sendPacket(teleport).addListener((ChannelFutureListener) future -> {
                    synchronized (this.pool) {
                        this.position = pos;
                    }
                });
                return;
            } else {
                p.net().sendPacket(teleport);
            }
        }
    }

    @Override
    public World getWorld() {
        return (World) this.getPosition().getWorld();
    }

    @Override
    public final void remove() {
        World world = (World) this.position.getWorld();
        world.getEntitySet().remove(this);
        world.getOccupants().remove(this);

        Chunk chunk = world.getChunkAt(this.position.getChunkX(), this.position.getChunkZ(), false);
        if (chunk != null) {
            if (this instanceof Player) {
                chunk.getOccupants().remove(this);
            } else {
                chunk.getEntitySet().remove(this);
            }
        }

        if (this instanceof Player) {
            world.getOccupants().remove(this);
        } else {
            world.getEntitySet().remove(this);
        }

        this.doRemove();

        PlayOutDestroyEntities destroyEntities = new PlayOutDestroyEntities(Collections.singletonList(this));
        VeraPlayer.getPlayers().values().stream().filter(player -> !player.equals(this)).forEach(p -> p.net().sendPacket(destroyEntities));
    }

    public final void tick() {
        this.pool.execute(this.tickingTask);
    }

    @Override
    public void updateMetadata() {
        PlayOutEntityMetadata packet = new PlayOutEntityMetadata(this);
        RecipientSelector.whoCanSee(this, false, packet);
    }

    public abstract void doRemove();

    public abstract void doTick();

    public abstract PacketOut getSpawnPacket();
}
