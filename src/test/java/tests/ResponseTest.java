package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.response.Response;
import io.thunder.packet.impl.response.ResponseStatus;
import packets.ResponseExamplePacket;

import java.util.function.Consumer;

public class ResponseTest {

    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ResponseExamplePacket) {
                    ResponseExamplePacket responseExamplePacket = (ResponseExamplePacket)packet;
                    responseExamplePacket.respond(ResponseStatus.SUCCESS, (responseExamplePacket.getA() + responseExamplePacket.getB())); //Responding to the Packet
                }
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {

                ResponseExamplePacket responseExamplePacket = new ResponseExamplePacket(20, 40);

                Response response = client.transferToResponse(responseExamplePacket); //Waiting for the response
                System.out.println("[Math] " + responseExamplePacket.getA() + " + " + responseExamplePacket.getB() + " = " + response.get(0).asInt());
            }
        });
    }
}
