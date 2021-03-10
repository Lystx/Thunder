
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.world.other.Position;
import io.vera.inventory.Substance;
import io.vera.event.player.PlayerDigEvent;
import io.vera.server.VeraServer;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.server.world.Block;
import io.vera.world.opt.GameMode;


public class PlayInPlayerDig extends PacketIn {

    public static final int MAX_DIST_SQ = 36;

    public PlayInPlayerDig() {
        super(PlayInPlayerDig.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        DigStatus status = DigStatus.values()[NetData.rvint(buf)];
        VeraPlayer player = client.getPlayer();
        Position position = NetData.rvec(buf).toPosition(player.getWorld());
        DigFace face = DigFace.get(buf.readByte());

        if (status == DigStatus.START_DIGGING && player.getGameMode() == GameMode.CREATIVE) {
            Block block = position.getBlock();
            if (position.distanceSquared(player.getPosition()) > MAX_DIST_SQ) {
                VeraServer.getInstance().getLogger().warn(
                        "Suspicious dig @ " + position + " by " + player.getName() + " (" + player.getUuid() + ')');
                return;
            }

            VeraServer.getInstance().getEventController().callEvent(new PlayerDigEvent(player, block), e -> {
                if (!e.isCancelled()) {
                    block.setSubstance(Substance.AIR);
                }
            });
            return;
        }

        if (status == DigStatus.FINISH_DIGGING) {
            Block block = position.getBlock();
            if (position.distanceSquared(player.getPosition()) > MAX_DIST_SQ) {
                VeraServer.getInstance().getLogger().warn(
                        "Suspicious dig @ " + position + " by " + player.getName() + " (" + player.getUuid() + ')');
                return;
            }

            VeraServer.getInstance().getEventController().callEvent(new PlayerDigEvent(player, block), e -> {
                if (!e.isCancelled()) {
                    block.setSubstance(Substance.AIR);
                }
            });
        }
    }
    
    private enum DigStatus {

        START_DIGGING,
        CANCEL_DIGGING,
        FINISH_DIGGING,
        DROP_ITEM_STACK,
        DROP_ITEM,
        SHOOT_ARROW_FINISH_EATING,
        SWAP_ITEM_IN_HAND
    }
    
    public enum DigFace {
        BOTTOM,
        TOP,
        NORTH,
        SOUTH,
        WEST,
        EAST,
        SPECIAL;
        
        public static DigFace get(int face){
            if(face <= 5){
                return values()[face];
            }
            
            return SPECIAL;
        }
    }
}