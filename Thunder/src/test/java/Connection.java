import io.thunder.Thunder;
import io.thunder.connection.ThunderClient;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.ThunderListener;
import io.thunder.connection.ThunderServer;
import io.thunder.manager.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

public class Connection {

    private static final List<ThunderConnection> CONNECTIONS = new LinkedList<>();

    public static void main(String[] args) {


        final ThunderServer server = Thunder.createServer();
        server.addHandler(new ThunderListener() {
            @Override
            public void handleConnect(ThunderConnection thunderConnection) {
                System.out.println("[Server] Ein neuer client hat sich verbunden");
                for (int i = 0; i < 10; i++) {
                    ((ThunderClient)thunderConnection).writePacket(new ExamplePacket("ClientVerbindung#" + UUID.randomUUID()));
                }
            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {

            }

            @Override
            public void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException {

            }

            @Override
            public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {

            }
        });
        server.start(1758);


        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            CONNECTIONS.add(Thunder.createClient(new ThunderListener() {
                @Override
                public void handleConnect(ThunderConnection thunderConnection) {

                }

                @Override
                public void handleDisconnect(ThunderConnection thunderConnection) {

                }

                @Override
                public void handlePacket(ThunderPacket packet, ThunderConnection thunderConnection) throws IOException {
                    System.out.println(packet.toString());
                }

                @Override
                public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {

                }
            }));

        }
        for (ThunderConnection connection : CONNECTIONS) {
            ((ThunderClient)connection).connect("localhost", 1758);
        }

        System.out.println("[Ergebnis] " + (System.currentTimeMillis() - start) + " ms");
    }



    @Getter @AllArgsConstructor
    public static class ExamplePacket extends BufferedPacket {

        private String name;

        @Override
        public void write(PacketBuffer buffer) {
            buffer.writeString(name);
        }

        @Override
        public void read(PacketReader packetReader) {
            this.name = packetReader.readString();
        }

        @Override
        public int getPacketID() {
            return 0x00;
        }
    }
}
