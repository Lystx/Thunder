import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.response.Response;
import io.thunder.manager.packet.response.ResponseStatus;

import java.io.IOException;
import java.util.function.Consumer;

public class ConnectionTest {


    public static void main(String[] args) {
        ThunderServer thunderServer = Thunder.createServer(new ThunderListener() {
            @Override
            public void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
                if (packet instanceof SamplePacket) {
                    packet.respond(ResponseStatus.SUCESS, "Packet Received");
                }
            }

            @Override
            public void handleConnect(ThunderConnection thunderConnection) {
                System.out.println("[Server] New Connection from " + thunderConnection + "!");
            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {
                System.out.println("[Server] " + thunderConnection + " has disconnected!");
            }
        });
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.start(1401).perform();

        thunderClient.connect("127.0.0.1", 1401).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient thunderClient) {
                thunderClient.sendPacket(new SamplePacket("Luca", 16), new Consumer<Response>() {
                    @Override
                    public void accept(Response response) {
                        System.out.println(response.getStatus() + " - " + response.getMessage() + " [" + response.getProcessingTime() + "ms]");
                    }
                });
            }
        });
    }
}
