package io.vera.server.packet.status;

import io.netty.buffer.ByteBuf;
import io.vera.event.base.Event;
import io.vera.event.server.ServerPingEvent;
import io.vera.server.VeraServer;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.config.ServerConfig;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class StatusInRequest extends PacketIn {
    public StatusInRequest() {
        super(StatusInRequest.class);
    }

    public void read(ByteBuf buf, NetClient client) {
        ServerConfig cfg = VeraServer.cfg();
        Collection<VeraPlayer> players = VeraPlayer.getPlayers().values();
        int onlinePlayers = players.size();
        ServerPingEvent.ServerPingResponseSample[] sample = new ServerPingEvent.ServerPingResponseSample[onlinePlayers];
        int i = 0;
        for (VeraPlayer player : players)
            sample[i++] = new ServerPingEvent.ServerPingResponseSample(player.getName(), player.getUuid());
        ServerPingEvent.ServerPingResponse response = new ServerPingEvent.ServerPingResponse(new ServerPingEvent.ServerPingResponseVersion("1.8.9", 47), new ServerPingEvent.ServerPingResponsePlayers(onlinePlayers, cfg.maxPlayers(), sample), ChatComponent.text(cfg.motd()), StatusOutResponse.b64icon.get());
        InetSocketAddress pinger = (InetSocketAddress)client.getChannel().remoteAddress();
        ServerPingEvent event = new ServerPingEvent(pinger, response);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS)
                .submit(() -> VeraServer.getInstance().getEventController().callEvent(event, (Consumer<Event>) event1 -> {

                }));
    }
}
