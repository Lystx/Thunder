package io.vera.server.world;

import io.vera.inventory.Substance;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutBlockChange;
import io.vera.server.player.RecipientSelector;
import io.vera.world.other.Position;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Block {
  private final Position position;
  
  private final World world;
  
  private final int cX;
  
  private final int cZ;
  
  private final int relX;
  
  private final int relY;
  
  private final int relZ;
  
  public Position getPosition() {
    return this.position;
  }
  
  public Block(Position position) {
    this.position = position;
    this.world = position.getWorld();
    this.cX = position.getIntX() >> 4;
    this.cZ = position.getIntZ() >> 4;
    this.relX = position.getIntX() & 0xF;
    this.relY = position.getIntY();
    this.relZ = position.getIntZ() & 0xF;
  }
  
  public Substance getSubstance() {
    return Substance.fromNumericId(getChunk().get(this.relX, this.relY, this.relZ) >> 4);
  }
  
  public void setSubstance(Substance substance) {
    Chunk chunk = getChunk();
    short state = (short)(substance.getId() << 4);
    chunk.set(this.relX, this.relY, this.relZ, state);
    RecipientSelector.whoCanSee(chunk, null, new PacketOut[] { (PacketOut)new PlayOutBlockChange(this.position, state) });
  }
  
  public byte getData() {
    return (byte)(getChunk().get(this.relX, this.relY, this.relZ) & 0xF);
  }
  
  public void setData(byte data) {
    Chunk chunk = getChunk();
    int substanceId = chunk.get(this.relX, this.relY, this.relZ) >> 4;
    short state = (short)(substanceId << 4 | data & 0xF);
    chunk.set(this.relX, this.relY, this.relZ, state);
    RecipientSelector.whoCanSee(chunk, null, new PacketOut[] { (PacketOut)new PlayOutBlockChange(this.position, state) });
  }
  
  public void setSubstanceData(Substance substance, byte data) {
    Chunk chunk = getChunk();
    short state = (short)(substance.getId() << 4 | data & 0xF);
    chunk.set(this.relX, this.relY, this.relZ, state);
    RecipientSelector.whoCanSee(chunk, null, new PacketOut[] { (PacketOut)new PlayOutBlockChange(this.position, state) });
  }
  
  private Chunk getChunk() {
    return this.world.getChunkAt(this.cX, this.cZ);
  }
}
