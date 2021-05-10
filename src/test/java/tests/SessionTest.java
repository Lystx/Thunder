package tests;

import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import packets.BasicExamplePacket;

import java.io.IOException;
import java.util.function.Consumer;

public class SessionTest {

    public static void main(String[] args) {

        Thunder.setLogging(LogLevel.ERROR);

        //Creating Instances for Client & Server
        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();


        thunderServer.addSessionListener(new ThunderListener() {
            @Override
            public void handleConnect(ThunderSession session) {
                System.out.println("[Server] A New Client has connected : ");
                System.out.println("Name: " + session.getSessionId());
                System.out.println("UUID: " + session.getUniqueId());
                System.out.println("StartTime: " + session.getStartTime());
                System.out.println("Connections: " + session.getConnectedSessions().size());
                System.out.println("Address (Remote): " + session.getChannel().remoteAddress());
                System.out.println("Address (Local): " + session.getChannel().localAddress());

                session.disconnect(); //Stops the connection

            }

            @Override
            public void handleDisconnect(ThunderSession session) {
                System.out.println("[Server] A Client has disconnected with UUID " + session.getUniqueId());

                thunderServer.disconnect(); //Stops the server

            }
        });

        thunderServer.start(1401).perform(); //Starting server
        thunderClient.connect("localhost", 1401).perform();
    }
}
