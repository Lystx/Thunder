package io.thunder.impl.connection;


import io.thunder.Thunder;
import io.thunder.codec.PacketCodec;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.codec.PacketPreDecoder;
import io.thunder.codec.impl.DefaultPacketDecoder;
import io.thunder.codec.impl.DefaultPacketEncoder;
import io.thunder.codec.impl.DefaultPacketPreDecoder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.impl.channel.ServerThunderChannel;
import io.thunder.impl.other.ProvidedThunderAction;
import io.thunder.impl.other.ProvidedThunderSession;
import io.thunder.manager.factory.ThunderFactoryClient;
import io.thunder.manager.factory.ThunderFactoryServerSocket;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.object.ObjectHandler;
import io.thunder.packet.object.PacketObject;
import io.thunder.utils.ThunderAction;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.thunder.Thunder.LOGGER;

@Getter
public class ProvidedThunderServer implements ThunderServer {


    private final ThunderFactoryServerSocket ssf;
    private final ThunderFactoryClient cf;

    private ServerSocket server;
    private final List<ThunderClient> clients;
    private final PacketAdapter packetAdapter;
    private final ThunderSession session;
    private final ThunderChannel channel;

    private PacketEncoder encoder = new DefaultPacketEncoder();
    private PacketDecoder decoder = new DefaultPacketDecoder();
    private PacketPreDecoder preDecoder = new DefaultPacketPreDecoder();

    private ThunderListener thunderListener;
    private final List<ObjectHandler<?>> objectHandlers;


    private ProvidedThunderServer(ThunderFactoryServerSocket ssf, ThunderFactoryClient cf) {
        this.ssf = ssf;
        this.cf = cf;

        this.clients = new LinkedList<>();
        this.packetAdapter = new PacketAdapter();
        this.objectHandlers = new ArrayList<>();

        this.session = ProvidedThunderSession.newInstance("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this,null, false);
        this.channel = ServerThunderChannel.newInstance(this);
    }

    public static ThunderServer newInstance(ThunderFactoryServerSocket ssf, ThunderFactoryClient cf) {
        return new ProvidedThunderServer(ssf, cf);
    }

    @Override
    public synchronized void setName(String name) {
        this.session.setSessionId(name);
    }

    @Override
    public synchronized void addSessionListener(ThunderListener serverListener) {
        this.thunderListener = serverListener;
    }

    @Override @SneakyThrows
    public synchronized void sendObject(Object object) {
        this.sendPacket(new PacketObject<>(object, System.currentTimeMillis()));
    }

    @Override
    public synchronized void addObjectListener(ObjectHandler<?> objectHandler) {
        this.objectHandlers.add(objectHandler);
    }

    @Override
    public synchronized ThunderAction<ThunderServer> start(int port) {
        return ProvidedThunderAction.newInstance(thunderServer -> {
            LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server starting...");

            try {
                this.server = ssf.getSocket(port);
                this.session.setAuthenticated(true);
                this.session.setChannel(this.channel);

                LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server-Listener-Thread started!");
                Thunder.EXECUTOR_SERVICE.submit(() -> {

                    while (this.isConnected()) {
                        try {
                            Socket socket = server.accept();
                            ThunderClient thunderClient = cf.getThunderClient();

                            thunderClient.addSessionListener(new ThunderListener() {
                                @Override
                                public void handleConnect(ThunderSession thunderSession) {
                                    synchronized (clients) {
                                        LOGGER.log(LogLevel.DEBUG, "(Server-Side) ThunderClient " + thunderSession + " has connected!");
                                        clients.add((ThunderClient) thunderSession.getConnection());
                                        session.getConnectedSessions().add(thunderSession);
                                    }
                                    if (thunderListener != null) {
                                        thunderListener.handleConnect(thunderSession);
                                    }
                                }

                                @Override
                                public void handleDisconnect(ThunderSession thunderSession) {
                                    synchronized (clients) {
                                        LOGGER.log(LogLevel.DEBUG, "(Server-Side) ThunderClient " + thunderSession + " has disconnected!");
                                        ThunderConnection connection = session.getSession(thunderSession.getUniqueId()).getConnection();
                                        if (connection == null) {
                                            Thunder.LOGGER.log(LogLevel.ERROR, "Couldn't disconnect " + thunderSession + " because the given ThunderClient was never registered!");
                                            return;
                                        }
                                        clients.remove((ThunderClient) connection);
                                        session.getConnectedSessions().remove(thunderSession);
                                    }
                                    if (thunderListener != null) {
                                        thunderListener.handleDisconnect(thunderSession);
                                    }
                                }

                                @Override
                                public void readPacket(Packet packet, ThunderConnection thunderClient) throws IOException {
                                    if (thunderListener != null) {
                                        LOGGER.log(LogLevel.DEBUG, "(Server-Side) ThunderServer has received Packet " + packet);
                                        thunderListener.readPacket(packet, ProvidedThunderServer.this);
                                    }
                                }
                            });

                            thunderClient.setSocket(socket);
                        } catch (SocketException e) {
                            this.disconnect();
                            break;
                        } catch (IOException e) {
                            LOGGER.log(LogLevel.ERROR, "(Server-Side) Error in Server-Listener-Thread!");
                            this.disconnect();
                            break;
                        }
                    }

                    LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server-Listener-Thread stopped!");
                });
            } catch (Exception e) {
                if (LOGGER.getLogLevel().equals(LogLevel.ERROR) || LOGGER.getLogLevel().equals(LogLevel.ALL)) {
                    LOGGER.log(LogLevel.ERROR, "(Server-Side) Server couldn't start! :");
                    e.printStackTrace();
                }

            }
        }, this);
    }


    @Override
    public String toString() {
        return asString();
    }

    @Override
    public synchronized void sendPacket(Packet packet) {
        this.channel.processOut(packet);
    }

    @SneakyThrows @Override
    public synchronized void flush() {
        this.channel.flush();
    }

    @Override @SneakyThrows
    public synchronized void sendPacket(Packet packet, ThunderClient client) {
        this.channel.processOut(packet, client.getChannel());
    }

    @Override
    public synchronized void sendPacket(Packet packet, ThunderSession session) {
        ThunderClient thunderClient = this.clients.stream().filter(thunderClient1 -> thunderClient1.getSession().getUniqueId().equals(session.getUniqueId())).findFirst().orElse(null);
        if (thunderClient == null) {
            return;
        }
        this.sendPacket(packet, thunderClient);
    }

    @Override
    public synchronized void disconnect() {

        LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server stopping...");

        synchronized (clients) {
            for (ThunderClient thunderClient : clients) {
                thunderClient.addSessionListener(null);
                thunderClient.disconnect();
            }
            clients.clear();
        }

        this.session.getConnectedSessions().clear();
        this.session.setAuthenticated(false);
        this.session.setChannel(null);

        if (server == null) {
            return;
        }
        try {
            server.close();
            LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server closed!");
        } catch (Exception e) {
            LOGGER.log(LogLevel.ERROR, "(Server-Side) Server couldn't be closed!");
        }
    }


    @Override
    public synchronized void addCodec(PacketCodec packetCodec) {
        if (packetCodec instanceof PacketEncoder) {
            encoder = (PacketEncoder) packetCodec;
        } else if (packetCodec instanceof PacketPreDecoder) {
            preDecoder = (PacketPreDecoder) packetCodec;
        } else {
            decoder = (PacketDecoder) packetCodec;
        }
    }

    @Override
    public synchronized boolean isConnected() {
        return this.server.isBound();
    }

}

