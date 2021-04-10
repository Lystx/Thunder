package org.gravel.handler;

import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.GravelServer;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.networking.connection.adapter.PacketHandlerAdapter;
import org.gravel.library.manager.networking.packets.PacketInBoundHandler;
import org.gravel.library.manager.networking.packets.Result;

@Getter @AllArgsConstructor
public class PacketHandlerResult extends PacketHandlerAdapter {

    private final GravelServer gravelServer;

    @Override
    public void handle(ThunderPacket packet) {
        if (packet instanceof PacketInBoundHandler) {
            PacketInBoundHandler<Object> resultPacket = (PacketInBoundHandler<Object>)packet;

            Result<Object> result = new Result<>(resultPacket.getUuid(), resultPacket.handleRead(GravelAPI.getInstance()));

            resultPacket.setResult(result);
            GravelAPI.getInstance().sendPacket(resultPacket);

        }
    }
}
