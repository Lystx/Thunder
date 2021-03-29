package de.lystx.messenger.handler;

import de.lystx.messenger.Client;
import de.lystx.messenger.networking.packet.PacketHandler;
import de.lystx.messenger.networking.packets.out.PacketOutClientConnected;
import lombok.SneakyThrows;


public class ConnectionHandler {

    boolean connected;

    @SneakyThrows
    @PacketHandler
    public void handle(PacketOutClientConnected packet) {
        if (!connected) {
            connected = true;
            Client.getInstance().getConsole().sendMessage("INFO", "&aConnected to &2Messenger&8!");
            Client.getInstance().onConnect();
        }
    }

}
