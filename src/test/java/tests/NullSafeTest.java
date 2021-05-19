package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.impl.other.DefaultPacketCompressor;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.utils.logger.LogLevel;
import packets.NullSafeExamplePacket;

import java.util.function.Consumer;

public class NullSafeTest {


    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof NullSafeExamplePacket) {
                    System.out.println("[NullSafeExamplePacket] Received : " + packet);
                    NullSafeExamplePacket nullSafeExamplePacket = (NullSafeExamplePacket)packet;
                    System.out.println("[--->] " + nullSafeExamplePacket.getUuid() + ":" + nullSafeExamplePacket.getName());
                }
            }
        });


        thunderServer.addCompressor(new DefaultPacketCompressor());

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).performAsync(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {

                NullSafeExamplePacket packet = new NullSafeExamplePacket(null, null);
                client.sendPacket(packet);

            }
        });

    }

}
