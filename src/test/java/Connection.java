import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.ThunderPacket;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class Connection {

    public static void main(String[] args) {

        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.start(1401).perform();
        thunderServer.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderConnection thunderConnection) {

            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {

            }

            @Override
            public void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException {
                System.out.println(packet);
                if (packet instanceof PacketMenschCreate) {
                    PacketMenschCreate packetMenschCreate = (PacketMenschCreate)packet;
                    final Mensch mensch = packetMenschCreate.getMensch();
                    System.out.println(mensch);
                }
            }

            @Override
            public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {

            }
        });

        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient thunderClient) {
                thunderClient.sendPacket(new PacketMenschCreate(new Mensch("Lystx", "Heeg", 16, UUID.randomUUID())));
            }
        });


    }
}
