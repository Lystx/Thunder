package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import io.vera.server.world.World;
import io.vera.world.opt.Difficulty;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutDifficulty extends PacketOut {
  private final Difficulty difficulty;
  
  public PlayOutDifficulty(World world) {
    super(PlayOutDifficulty.class);
    this.difficulty = world.getWorldOptions().getDifficulty();
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.difficulty.asByte());
  }
}
