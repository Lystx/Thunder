package tests;

import io.thunder.Thunder;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.impl.object.ObjectHandler;
import io.thunder.utils.objects.Serializer;
import io.thunder.utils.objects.ThunderTimer;
import packets.ExampleObjectPacket;
import utils.BaseObject;
import utils.ExampleObject;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class ObjectTest {

    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();


        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ExampleObjectPacket) {
                    ExampleObjectPacket exampleObjectPacket = (ExampleObjectPacket)packet;
                    System.out.println(exampleObjectPacket.getExampleObject().getData());
                    System.out.println(exampleObjectPacket.getExampleObject().getName());
                    System.out.println(exampleObjectPacket.getExampleObject().getStamp());
                    System.out.println("[Packet] Received exampleObject in " + packet.getProcessingTime() + "ms");
                    exampleObjectPacket.respond(ResponseStatus.SUCCESS, packet.getProcessingTime() + "ms");
                }
            }
        });

        thunderServer.addObjectListener(new ObjectHandler<ExampleObject>() {
            @Override
            public void readChannel(ThunderChannel channel, ExampleObject exampleObject, long time) {
                System.out.println("[Object] Received ExampleObject in " + time + "ms");
            }

            @Override
            public Class<ExampleObject> getObjectClass() {
                return ExampleObject.class;
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {
                client.sendPacket(new ExampleObjectPacket(new ExampleObject("Lystx", UUID.randomUUID(), System.currentTimeMillis(), Arrays.asList("a", "b", "c", "d", "e", "f"))), response -> System.out.println(response.getMessage()));
               // client.sendObject(new ExampleObject("Lystx", UUID.randomUUID(), System.currentTimeMillis(), Arrays.asList("a", "b", "c", "d", "e", "f")));
            }
        });
    }
}
