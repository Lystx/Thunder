import io.lightning.network.connection.LightningBase;
import io.lightning.network.connection.client.LightningClient;
import io.lightning.network.connection.server.LightningServer;
import io.lightning.network.packet.Packet;
import io.lightning.network.packet.PacketHandler;
import io.lightning.network.utility.other.LightningSettings;

import java.util.function.Consumer;


public class ConnectionTest {


    public static void main(String[] args) {
        try {

            final LightningBase<?> build = new LightningServer()
                    .option(LightningSettings.LOG_ENABLED)
                    .option(LightningSettings.USE_TCP)
                    .option(LightningSettings.FIRE_EXCEPTIONS)
                    .option(LightningSettings.ASYNC_AND_USE_THREAD)
                    .registerHandler(new PacketHandler() {
                        @Override
                        public void handle(Packet packet) {
                            if (packet instanceof ExamplePacket) {
                                ExamplePacket examplePacket = ((ExamplePacket) packet);
                                System.out.println("[Server] Registered new User (" + examplePacket.getName() + ":" + examplePacket.getAlter() + ")");
                            }
                        }
                    })
                    .build("127.0.0.1", 1758);

            new LightningClient()
                    .option(LightningSettings.LOG_ENABLED)
                    .option(LightningSettings.USE_TCP)
                    .option(LightningSettings.FIRE_EXCEPTIONS)
                    .option(LightningSettings.ASYNC_AND_USE_THREAD)
                    .registerHandler(new PacketHandler() {
                        @Override
                        public void handle(Packet packet) {

                            if (packet instanceof ExamplePacket) {
                                ExamplePacket examplePacket = ((ExamplePacket) packet);
                                System.out.println("[Client] Registered new User (" + examplePacket.getName() + ":" + examplePacket.getAlter() + ")");
                            }
                        }
                    })
                    .onConnect(new Consumer<LightningClient>() {
                        @Override
                        public void accept(LightningClient client) {
                            client.queue(new ExamplePacket("Luca", 16)).flush();
                            client.queue(new ExamplePacket("Jonas", 16)).flush();
                            build.queue(new ExamplePacket("Lystx", 15)).flush();
                        }
                    })
                    .build("localhost", 1758);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
