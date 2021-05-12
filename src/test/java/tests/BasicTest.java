package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.impl.connection.ProvidedThunderClient;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import packets.BasicExamplePacket;

import java.util.function.Consumer;

public class BasicTest {


    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof BasicExamplePacket) {
                    System.out.println("[BasicExamplePacket] Received : " + packet);
                    BasicExamplePacket basicExamplePacket = (BasicExamplePacket)packet;
                    System.out.println("[--->] " + basicExamplePacket.getName() + ":" + basicExamplePacket.getAge());
                }
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {

                BasicExamplePacket packet = new BasicExamplePacket("ExampleName", 32);
                client.sendPacket(packet);

            }
        });

    }

}
