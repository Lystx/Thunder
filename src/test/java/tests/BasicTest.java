package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.base.ThunderSession;
import io.thunder.impl.other.DefaultPacketCompressor;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.utils.logger.LogLevel;
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


        thunderServer.addCompressor(new DefaultPacketCompressor());

        thunderClient.addSessionListener(new ThunderListener() {
            @Override
            public void handleConnect(ThunderSession session) {
                System.out.println("[Client] Connected to ThunderServer (" + System.currentTimeMillis() + ")");

            }

            @Override
            public void handleHandshake(PacketHandshake handshake) {
                System.out.println("[Client] Received HandShake from ThunderServer! (" + System.currentTimeMillis() + ")");
            }

            @Override
            public void handlePacketSend(Packet packet) {
                System.out.println("[Client] Sending " + packet.getClass().getSimpleName() + "... (" + System.currentTimeMillis() + ")");
            }

            @Override
            public void handlePacketReceive(Packet packet) {
                System.out.println("[Client] Received " + packet.getClass().getSimpleName() + "! (" + System.currentTimeMillis() + ")");
            }

            @Override
            public void handleDisconnect(ThunderSession session) {
                System.out.println(thunderClient.toString());
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).performAsync(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {

                BasicExamplePacket packet = new BasicExamplePacket("ExampleName", 32);
                client.sendPacket(packet);

            }
        });

    }

}
