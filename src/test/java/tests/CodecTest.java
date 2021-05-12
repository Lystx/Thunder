package tests;

import io.thunder.Thunder;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.codec.PacketPreDecoder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.utils.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.handler.PacketHandler;
import packets.CodecExamplePacket;

import java.io.DataOutputStream;
import java.util.function.Consumer;

public class CodecTest {

    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof CodecExamplePacket) {
                    System.out.println("[CodecExamplePacket] Received : " + packet);
                }
            }
        });

        thunderClient.addCodec(new PacketPreDecoder() {
            @Override
            public Packet decode(PacketBuffer buf) throws Exception {
                return null;
            }
        });

        thunderClient.addCodec(new PacketEncoder() {
            @Override
            public void encode(Packet packet, DataOutputStream dataOutputStream, PacketBuffer buf) throws Exception {

            }
        });


        thunderClient.addCodec(new PacketDecoder() {
            @Override
            public Packet decode(Packet packet, PacketBuffer buf, ThunderConnection thunderConnection) throws Exception {
                return null;
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {

                CodecExamplePacket packet = new CodecExamplePacket(true, 30, "This is a String");
                client.sendPacket(packet);

            }
        });
    }
}
