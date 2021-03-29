package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.entity.living.Player;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.world.World;
import io.vera.world.opt.Dimension;
import io.vera.world.opt.GameRule;
import io.vera.world.opt.LevelType;
import io.vera.world.opt.WorldOpts;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayOutJoinGame extends PacketOut {
  private final WorldOpts opts;
  
  private final Dimension dimension;
  
  private final LevelType type;
  
  private final Player player;
  
  public PlayOutJoinGame(Player player, World world) {
    super(PlayOutJoinGame.class);
    this.player = player;
    this.dimension = world.getDimension();
    this.opts = (WorldOpts)world.getWorldOptions();
    this.type = world.getGeneratorOptions().getLevelType();
  }
  
  public void write(ByteBuf buf) {
    buf.writeInt(this.player.getId());
    buf.writeByte(this.opts.getGameMode().asByte());
    buf.writeInt(this.dimension.asByte());
    buf.writeByte(this.opts.getDifficulty().asByte());
    buf.writeByte(0);
    NetData.wstr(buf, this.type.toString());
    buf.writeBoolean(((Boolean)this.opts.getGameRules().get(GameRule.REDUCE_DEBUG)).booleanValue());
  }
}
