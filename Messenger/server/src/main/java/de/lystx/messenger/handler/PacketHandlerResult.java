package de.lystx.messenger.handler;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.Server;
import de.lystx.messenger.networking.connection.adapter.PacketHandlerAdapter;
import de.lystx.messenger.networking.connection.packet.Packet;
import de.lystx.messenger.networking.packets.Result;
import de.lystx.messenger.networking.packets.PacketInBoundHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerResult extends PacketHandlerAdapter {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInBoundHandler) {
            PacketInBoundHandler<Object> packetInBoundHandler = (PacketInBoundHandler<Object>)packet;
            Result<Object> result = new Result<>(packetInBoundHandler.getUniqueId(), packetInBoundHandler.handleRead(MessageAPI.getInstance()));
            packetInBoundHandler.setResult(result);
            Server.getInstance().getConnectionServer().sendPacket(packetInBoundHandler);
        }
    }

}
