
package io.vera.server.packet.status;

import io.netty.buffer.ByteBuf;
import io.vera.event.server.ServerPingEvent;
import io.vera.server.VeraServer;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.config.ServerConfig;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;
import java.net.InetSocketAddress;
import java.util.Collection;


@Immutable
public final class StatusInRequest extends PacketIn {
    public StatusInRequest() {
        super(StatusInRequest.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        ServerConfig cfg = VeraServer.cfg();

        Collection<VeraPlayer> players = VeraPlayer.getPlayers().values();
        int onlinePlayers = players.size();
        ServerPingEvent.ServerPingResponseSample[] sample = new ServerPingEvent.ServerPingResponseSample[onlinePlayers];
        int i = 0;
        for (VeraPlayer player : players) {
            sample[i++] = new ServerPingEvent.ServerPingResponseSample(player.getName(), player.getUuid());
        }

        ServerPingEvent.ServerPingResponse response = new ServerPingEvent.ServerPingResponse(
                new ServerPingEvent.ServerPingResponseVersion(StatusOutResponse.MC_VERSION, StatusOutResponse.PROTOCOL_VERSION),
                new ServerPingEvent.ServerPingResponsePlayers(onlinePlayers, cfg.maxPlayers(), sample),
                ChatComponent.text(cfg.motd()),
                StatusOutResponse.b64icon.get()
        );
        InetSocketAddress pinger = (InetSocketAddress) client.getChannel().remoteAddress();
        ServerPingEvent event = new ServerPingEvent(pinger, response);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS)
                .submit(() -> VeraServer.getInstance().getEventController()
                        .callEvent(event, e -> client.sendPacket(new StatusOutResponse(e)))
        );
    }
}
