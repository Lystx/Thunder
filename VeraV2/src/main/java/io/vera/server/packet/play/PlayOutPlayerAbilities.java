package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import io.vera.server.player.VeraPlayer;
import io.vera.world.opt.GameMode;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PlayOutPlayerAbilities extends PacketOut {
  private final boolean isGod;
  
  private final boolean isFlying;
  
  private final boolean canFly;
  
  private final GameMode gameMode;
  
  private final float flyingSpeed;
  
  private final float walkingSpeed;
  
  private volatile boolean doubleJump;
  
  public PlayOutPlayerAbilities(VeraPlayer player) {
    super(PlayOutPlayerAbilities.class);
    this.isGod = player.isGodMode();
    this.isFlying = player.isFlying();
    this.canFly = player.canFly();
    this.gameMode = player.getGameMode();
    this.flyingSpeed = player.getFlyingSpeed();
    this.walkingSpeed = player.getWalkingSpeed();
    this.doubleJump = false;
  }
  
  public void setDoubleJumpInsteadOfFlying() {
    this.doubleJump = this.canFly;
  }
  
  public void write(ByteBuf buf) {
    byte abilities = 0;
    abilities = (byte)(abilities | (this.isGod ? 1 : 0));
    abilities = (byte)(abilities | ((this.isFlying && !this.doubleJump) ? 2 : 0));
    abilities = (byte)(abilities | (this.canFly ? 4 : 0));
    abilities = (byte)(abilities | ((this.gameMode == GameMode.CREATIVE) ? 8 : 0));
    buf.writeByte(abilities);
    buf.writeFloat(this.flyingSpeed);
    buf.writeFloat(this.walkingSpeed);
  }
}
