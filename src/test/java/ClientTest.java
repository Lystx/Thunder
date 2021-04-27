import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.ThunderPacket;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientTest {


    public static void main(String[] args) {
        ThunderClient thunderClient = Thunder.createClient();

        thunderClient.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderConnection thunderConnection) {

            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {
                System.out.println("[Client] Disconnected from Server! Stopping...");
                System.exit(0);
            }

            @Override
            public void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException {

            }

            @Override
            public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {

            }
        });

        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient thunderClient) {
                TestBufferedPacket packet = new TestBufferedPacket("Paul", 32);
                thunderClient.sendPacket(packet);


                TestUnbufferedPacket testUnbufferedPacket = new TestUnbufferedPacket("Hans", 56);
                thunderClient.sendPacket(testUnbufferedPacket);

                TestBufferedObjectPacket testBufferedObjectPacket = new TestBufferedObjectPacket(new TestBufferedObjectPacket.ExampleObject("OBjectName", UUID.randomUUID(), System.currentTimeMillis()));
                thunderClient.sendPacket(testBufferedObjectPacket);


            }
        });
    }
}
