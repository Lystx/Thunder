package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.object.ObjectHandler;
import packets.BasicExamplePacket;
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

        thunderServer.addObjectListener(new ObjectHandler<ExampleObject>() {
            @Override
            public void readChannel(ThunderChannel channel, ExampleObject exampleObject, long time) {
                System.out.println("Received ExampleObject in " + time + "ms");
            }

            @Override
            public Class<ExampleObject> getObjectClass() {
                return ExampleObject.class;
            }
        });

        thunderServer.addObjectListener(new ObjectHandler<String>() {
            @Override
            public void readChannel(ThunderChannel channel, String s, long time) {
                System.out.println("Received String (" + s + ") in " + time + "ms");
            }

            @Override
            public Class<String> getObjectClass() {
                return String.class;
            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() { //Connecting client and accepting consumer
            @Override
            public void accept(ThunderClient client) {
                client.sendObject("Test String");
                client.sendObject(new ExampleObject("Lystx", UUID.randomUUID(), System.currentTimeMillis(), Arrays.asList("a", "b", "c", "d", "e", "f")));
            }
        });
    }
}
