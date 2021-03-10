
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;


public class PlayInEntityAction extends PacketIn {

    public PlayInEntityAction() {
        super(PlayInEntityAction.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int entityId = NetData.rvint(buf);
        int actionId = NetData.rvint(buf);
        int jumpBoost = NetData.rvint(buf);

        VeraPlayer player = client.getPlayer();
        switch (actionId) {
            case 0: // start crouching
                player.setCrouching(true);
                break;
            case 1: // stop crouching
                player.setCrouching(false);
                break;
            case 2: // leave bed
                // TODO
                break;
            case 3: // start sprinting
                player.setSprinting(true);
                break;
            case 4: // stop sprinting
                player.setSprinting(false);
                break;
            case 5: // start jump w/ horse
                // TODO
                break;
            case 6: // stop jump w/ horse
                // TODO
                break;
            case 7: // open horse inventory
                // TODO
                break;
            case 8: // start flying w/ elytra
                // TODO
                break;
            default:
                break;
        }

        player.updateMetadata();
    }

}
