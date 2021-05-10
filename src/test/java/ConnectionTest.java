import io.thunder.Thunder;
import io.thunder.codec.PacketCodec;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.io.IOException;
import java.util.function.Consumer;

public class ConnectionTest {


    public static void main(String[] args) {

        ThunderServer thunderServer = Thunder.createServer(new ThunderListener() {
            @Override
            public void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
                System.out.println(packet.getProcessingTime() + "ms");
            }

            @Override
            public void handleConnect(ThunderConnection thunderConnection) {

            }

            @Override
            public void handleDisconnect(ThunderConnection thunderConnection) {

            }
        });


        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.start(1401).perform();

        thunderClient.connect("127.0.0.1", 1401).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient thunderClient) {
                SamplePacket samplePacket = new SamplePacket("Jonas", 10);
                samplePacket.sendAndFlush(thunderClient.getChannel());

            }
        });
    }
}
