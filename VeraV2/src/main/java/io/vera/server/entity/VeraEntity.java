package io.vera.server.entity;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import io.vera.entity.Entity;
import io.vera.meta.entity.EntityMeta;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.entity.meta.EntityMetaType;
import io.vera.server.entity.meta.VeraEntityMeta;
import io.vera.server.net.EntityMetadata;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutDestroyEntities;
import io.vera.server.packet.play.PlayOutEntityHeadLook;
import io.vera.server.packet.play.PlayOutEntityLookAndRelativeMove;
import io.vera.server.packet.play.PlayOutEntityMetadata;
import io.vera.server.packet.play.PlayOutEntityRelativeMove;
import io.vera.server.packet.play.PlayOutPosLook;
import io.vera.server.packet.play.PlayOutTeleport;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.Chunk;
import io.vera.server.world.World;
import io.vera.world.other.Position;
import io.vera.world.vector.AbstractVector;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class VeraEntity implements Entity {
  public static final AtomicInteger EID_COUNTER = new AtomicInteger();
  
  protected final ServerThreadPool pool;
  
  private final Runnable tickingTask = this::doTick;
  
  private final int id;
  
  @GuardedBy("pool")
  private Position position;
  
  private volatile boolean onGround;
  
  private final VeraEntityMeta metadata;
  
  public int getId() {
    return this.id;
  }
  
  public boolean isOnGround() {
    return this.onGround;
  }
  
  public void setOnGround(boolean onGround) {
    this.onGround = onGround;
  }
  
  public VeraEntityMeta getMetadata() {
    return this.metadata;
  }
  
  public VeraEntity(World world, PoolSpec spec) {
    this.id = EID_COUNTER.incrementAndGet();
    this.pool = ServerThreadPool.forSpec(spec);
    Position pos = world.getWorldOptions().getSpawn().toPosition(world);
    this.position = pos;
    if (this instanceof io.vera.entity.living.Player) {
      VeraPlayer player = (VeraPlayer)this;
      world.getOccupants().add(player);
      world.getChunkAt(pos.getChunkX(), pos.getChunkZ()).getOccupants().add(player);
    } else {
      world.getEntitySet().add(this);
    } 
    EntityMetaType metaType = getClass().<EntityMetaType>getAnnotation(EntityMetaType.class);
    if (metaType == null)
      throw new RuntimeException(getClass() + " doesn't have an EntityMetaType annotation!"); 
    try {
      this.metadata = metaType.value().getConstructor(new Class[] { EntityMetadata.class }).newInstance(new Object[] { new EntityMetadata() });
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public Position getPosition() {
    synchronized (this.pool) {
      return this.position;
    } 
  }
  
  public final void setPosition(Position position) {
    setPosition(position, true);
  }
  
  public void setPosition(Position position, boolean sendUpdate) {
    synchronized (this.pool) {
      Position old = this.position;
      World fromWorld = old.getWorld();
      World destWorld = position.getWorld();
      if (!destWorld.equals(fromWorld))
        if (this instanceof io.vera.entity.living.Player) {
          fromWorld.getOccupants().remove(this);
          destWorld.getOccupants().add((VeraPlayer)this);
        } else {
          fromWorld.getEntitySet().remove(this);
          destWorld.getEntitySet().add(this);
        }  
      int destCX = position.getChunkX();
      int destCZ = position.getChunkZ();
      Chunk destChunk = destWorld.getChunkAt(destCX, destCZ);
      int fromCX = old.getChunkX();
      int fromCZ = old.getChunkZ();
      if (fromCX != destCX || fromCZ != destCZ) {
        Chunk fromChunk = fromWorld.getChunkAt(fromCX, fromCZ, false);
        List<Entity> destroy = Collections.singletonList(this);
        PacketOut spawnThis = getSpawnPacket();
        if (this instanceof io.vera.entity.living.Player) {
          VeraPlayer player = (VeraPlayer)this;
          if (fromChunk != null)
            fromChunk.getOccupants().remove(player); 
          destChunk.getOccupants().add(player);
          Stream.<T>concat((fromChunk == null) ? Stream.<T>empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream())
            .distinct()
            .forEach(p -> {
                if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                  if (p.equals(this))
                    return; 
                  p.net().sendPacket(spawnThis);
                } 
                if (!destChunk.getHolders().contains(p))
                  p.net().sendPacket((PacketOut)new PlayOutDestroyEntities(destroy)); 
              });
          player.updateChunks(position);
        } else {
          if (fromChunk != null)
            fromChunk.getEntitySet().remove(this); 
          destChunk.getEntitySet().add(this);
          Stream.<T>concat((fromChunk == null) ? Stream.<T>empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream())
            .distinct()
            .forEach(p -> {
                if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                  if (p.equals(this))
                    return; 
                  p.net().sendPacket(spawnThis);
                } 
                if (!destChunk.getHolders().contains(p))
                  p.net().sendPacket((PacketOut)new PlayOutDestroyEntities(destroy)); 
              });
        } 
      } 
      Position delta = (Position)position.subtract((AbstractVector)old);
      if (delta.getX() != 0.0D || delta.getY() != 0.0D || delta.getZ() != 0.0D) {
        if (old.distanceSquared(position) > 16.0D) {
          teleport(destChunk, position, sendUpdate);
        } else if (Double.compare(old.getYaw(), position.getYaw()) == 0 && Double.compare(old.getPitch(), position.getPitch()) == 0) {
          PlayOutEntityRelativeMove packet = new PlayOutEntityRelativeMove(this, delta);
          updatePosition(destChunk, position, sendUpdate, new PacketOut[] { (PacketOut)packet });
        } else {
          PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
          PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
          updatePosition(destChunk, position, sendUpdate, new PacketOut[] { (PacketOut)lookAndRelativeMove, (PacketOut)look });
        } 
      } else if (Float.compare(old.getYaw(), position.getYaw()) != 0 || Float.compare(old.getPitch(), position.getPitch()) != 0) {
        PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
        PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
        updatePosition(destChunk, position, sendUpdate, new PacketOut[] { (PacketOut)lookAndRelativeMove, (PacketOut)look });
      } 
    } 
  }
  
  private void updatePosition(Chunk chunk, Position pos, boolean sendUpdate, PacketOut... packetOut) {
    if (chunk == null)
      throw new IllegalStateException("Player cannot inhabit an unloaded chunk"); 
    Set<VeraPlayer> targets = chunk.getHolders();
    Iterator<VeraPlayer> it = targets.iterator();
    while (it.hasNext()) {
      VeraPlayer p = it.next();
      if (p.equals(this)) {
        if (sendUpdate) {
          if (!it.hasNext()) {
            p.net().sendPacket((PacketOut)new PlayOutPosLook(p, pos)).addListener((GenericFutureListener)(future -> {
                  synchronized (this.pool) {
                    this.position = pos;
                  } 
                }));
            return;
          } 
          p.net().sendPacket((PacketOut)new PlayOutPosLook(p, pos));
          continue;
        } 
        synchronized (this.pool) {
          this.position = pos;
        } 
        continue;
      } 
      for (int i = 0; i < packetOut.length; i++) {
        PacketOut out = packetOut[i];
        if (!it.hasNext() && i == packetOut.length - 1) {
          p.net().sendPacket(out).addListener((GenericFutureListener)(future -> {
                synchronized (this.pool) {
                  this.position = pos;
                } 
              }));
          return;
        } 
        p.net().sendPacket(out);
      } 
    } 
  }
  
  private void teleport(Chunk chunk, Position pos, boolean sendUpdate) {
    if (chunk == null)
      throw new IllegalStateException("Player cannot inhabit an unloaded chunk"); 
    PlayOutTeleport teleport = new PlayOutTeleport(this, pos);
    Set<VeraPlayer> targets = chunk.getHolders();
    if (this instanceof VeraPlayer)
      targets.add((VeraPlayer)this); 
    Iterator<VeraPlayer> it = targets.iterator();
    while (it.hasNext()) {
      VeraPlayer p = it.next();
      if (p.equals(this)) {
        if (sendUpdate) {
          if (!it.hasNext()) {
            p.net().sendPacket((PacketOut)new PlayOutPosLook(p, pos));
            synchronized (this.pool) {
              this.position = pos;
            } 
            return;
          } 
          p.net().sendPacket((PacketOut)new PlayOutPosLook(p, pos));
          continue;
        } 
        synchronized (this.pool) {
          this.position = pos;
        } 
        continue;
      } 
      if (!it.hasNext()) {
        p.net().sendPacket((PacketOut)teleport).addListener((GenericFutureListener)(future -> {
              synchronized (this.pool) {
                this.position = pos;
              } 
            }));
        return;
      } 
      p.net().sendPacket((PacketOut)teleport);
    } 
  }
  
  public World getWorld() {
    return getPosition().getWorld();
  }
  
  public final void remove() {
    World world = this.position.getWorld();
    world.getEntitySet().remove(this);
    world.getOccupants().remove(this);
    Chunk chunk = world.getChunkAt(this.position.getChunkX(), this.position.getChunkZ(), false);
    if (chunk != null)
      if (this instanceof io.vera.entity.living.Player) {
        chunk.getOccupants().remove(this);
      } else {
        chunk.getEntitySet().remove(this);
      }  
    if (this instanceof io.vera.entity.living.Player) {
      world.getOccupants().remove(this);
    } else {
      world.getEntitySet().remove(this);
    } 
    doRemove();
    PlayOutDestroyEntities destroyEntities = new PlayOutDestroyEntities(Collections.singletonList(this));
    VeraPlayer.getPlayers().values().stream().filter(player -> !player.equals(this)).forEach(p -> p.net().sendPacket((PacketOut)destroyEntities));
  }
  
  public final void tick() {
    this.pool.execute(this.tickingTask);
  }
  
  public void updateMetadata() {
    PlayOutEntityMetadata packet = new PlayOutEntityMetadata(this);
    RecipientSelector.whoCanSee(this, false, new PacketOut[] { (PacketOut)packet });
  }
  
  public abstract void doRemove();
  
  public abstract void doTick();
  
  public abstract PacketOut getSpawnPacket();
}
