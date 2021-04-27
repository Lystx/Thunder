import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.ThunderPacket;
import io.thunder.manager.packet.handler.PacketHandler;

import java.io.IOException;

public class ServerTest {

    public static void main(String[] args) {

        ThunderServer thunderServer = Thunder.createServer();

        thunderServer.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderConnection thunderConnection) {
                System.out.println("[Server] New Connection : " + thunderConnection.toString() + "!");
            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {
                System.out.println("[Server] Connection (" + thunderConnection.toString() + ") disconnected!");
            }

            @Override
            public void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException {

                System.out.println(packet.toString());
                if (packet instanceof TestBufferedPacket) {
                    TestBufferedPacket testBufferedPacket = (TestBufferedPacket)packet;
                    System.out.println("[Server] TestBuffer: " + testBufferedPacket.getName() + " - " + testBufferedPacket.getAge());
                    thunderConnection.disconnect();
                } else if (packet instanceof TestUnbufferedPacket) {
                    //Takes longer to process
                    TestUnbufferedPacket testUnbufferedPacket = (TestUnbufferedPacket)packet;
                    System.out.println("[Server] TestUnBuffered: " + testUnbufferedPacket.getName() + " - " + testUnbufferedPacket.getAge());
                    thunderConnection.disconnect();
                } else if (packet instanceof TestBufferedObjectPacket) {
                    //Same as above
                }
            }

            @Override
            public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {}
        });

        thunderServer.start(1401).perform();
    }
}
