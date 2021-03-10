
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.PacketOut;
import io.vera.server.player.VeraPlayer;
import io.vera.world.opt.GameMode;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Sent after {@link PlayOutSpawnPos} to communicate to the
 * client their abilities once joined.
 */
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

    @Override
    public void write(ByteBuf buf) {
        byte abilities = 0x00;
        abilities |= this.isGod ? 0x01 : 0x00; // invuln
        abilities |= this.isFlying && !this.doubleJump ? 0x02 : 0; // flying
        abilities |= this.canFly ? 0x04 : 0; // can fly
        abilities |= this.gameMode == GameMode.CREATIVE ? 0x08 : 0; // creative

        buf.writeByte(abilities);
        buf.writeFloat(this.flyingSpeed);
        buf.writeFloat(this.walkingSpeed);
    }
}
