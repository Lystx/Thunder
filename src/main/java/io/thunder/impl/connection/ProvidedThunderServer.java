package io.thunder.impl.connection;


import io.thunder.Thunder;
import io.thunder.connection.codec.PacketCodec;
import io.thunder.connection.codec.PacketDecoder;
import io.thunder.connection.codec.PacketEncoder;
import io.thunder.connection.codec.PacketPreDecoder;
import io.thunder.connection.extra.PacketCompressor;
import io.thunder.impl.codec.DefaultPacketDecoder;
import io.thunder.impl.codec.DefaultPacketEncoder;
import io.thunder.impl.codec.DefaultPacketPreDecoder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.base.ThunderSession;
import io.thunder.impl.channel.ServerThunderChannel;
import io.thunder.impl.other.ProvidedThunderAction;
import io.thunder.impl.other.ProvidedThunderSession;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.packet.impl.response.Response;
import io.thunder.utils.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.impl.object.ObjectHandler;
import io.thunder.packet.impl.object.PacketObject;
import io.thunder.utils.ThunderAction;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
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
import java.util.function.Consumer;

import static io.thunder.Thunder.LOGGER;

@Getter
public class ProvidedThunderServer implements ThunderServer {


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
    private final List<PacketCompressor> packetCompressors;


    private ProvidedThunderServer() {

        this.clients = new LinkedList<>();
        this.packetAdapter = new PacketAdapter();
        this.objectHandlers = new ArrayList<>();
        this.packetCompressors = new ArrayList<>();

        this.session = ProvidedThunderSession.newInstance("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this,null, false);
        this.channel = ServerThunderChannel.newInstance(this);

    }

    public static ThunderServer newInstance() {
        return new ProvidedThunderServer();
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
    public synchronized void addCompressor(PacketCompressor compressor) {
        this.packetCompressors.add(compressor);
    }

    @Override
    public synchronized ThunderAction<ThunderServer> start(int port) {
        return ProvidedThunderAction.newInstance(thunderServer -> {
            LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server starting...");

            try {
                this.server = new ServerSocket(port);
                this.session.setHandShaked(true);
                this.session.setChannel(this.channel);

                LOGGER.log(LogLevel.DEBUG, "(Server-Side) Server-Listener-Thread started!");
                Thunder.EXECUTOR_SERVICE.submit(() -> {

                    while (this.isConnected()) {
                        try {
                            Socket socket = server.accept();
                            ThunderClient thunderClient = ThunderClient.newInstance();

                            thunderClient.addSessionListener(new ThunderListener() {
                                @Override
                                public void handleConnect(ThunderSession thunderSession) {
                                    synchronized (clients) {
                                        LOGGER.log(LogLevel.DEBUG, "(Server-Side) ThunderClient " + thunderSession + " has connected!");
                                        clients.add((ThunderClient) thunderSession.getConnection());


                                        //Handshaking
                                        PacketHandshake packetHandshake = new PacketHandshake();

                                        thunderServer.sendPacket(packetHandshake, thunderSession, new Consumer<Response>() {
                                            @Override
                                            public void accept(Response response) {

                                                String sessionId = response.get(0).asString();
                                                UUID uuid = response.get(1).asUUID();
                                                long time = response.get(2).asLong();

                                                thunderSession.setSessionId(sessionId.split("ThunderSession#")[1]);
                                                thunderSession.setUniqueId(uuid);
                                                thunderSession.setStartTime(time);
                                                thunderSession.setHandShaked(true);

                                                session.getConnectedSessions().add(thunderSession);
                                            }
                                        });

                                    }
                                    if (thunderListener != null) {
                                        thunderListener.handleConnect(thunderSession);
                                    }
                                }

                                @Override
                                public void handleHandshake(PacketHandshake handshake) {
                                    if (thunderListener != null) {
                                        thunderListener.handleHandshake(handshake);
                                    }
                                }

                                @Override
                                public void handlePacketSend(Packet packet) {
                                    if (thunderListener != null) {
                                        thunderListener.handlePacketSend(packet);
                                    }
                                }

                                @Override
                                public void handlePacketReceive(Packet packet) {
                                    if (thunderListener != null) {
                                        thunderListener.handlePacketReceive(packet);
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

        VsonObject vsonObject = new VsonObject();

        vsonObject.append("session",
                new VsonObject()
                .append("name", session.getSessionId())
                .append("uniqueId", session.getUniqueId())
                .append("startTime", session.getStartTime())
                .append("handShaked", session.isHandShaked())
        );
        vsonObject.append("connection",
                new VsonObject()
                .append("type", "SERVER")
                .append("connectedClients", this.clients.size())
                .append("channel",
                        new VsonObject()
                                .append("remoteAddress", this.channel.remoteAddress().toString())
                                .append("localAddress", this.channel.localAddress().toString())
                                .append("valid", this.channel.isValid())
                                .append("open", this.channel.isOpen())
                )
        );

        vsonObject.append("general",
                new VsonObject()
                        .append("listener", this.getThunderListener() != null)
                        .append("objectHandlers", this.getObjectHandlers().size())
        );

        vsonObject.append("codec",
                new VsonObject()
                .append("preDecoder", preDecoder.getClass().getName())
                .append("decoder", decoder.getClass().getName())
                .append("encoder", encoder.getClass().getName())
        );

        return vsonObject.toString(FileFormat.JSON);
    }

    @Override
    public synchronized void sendPacket(Packet packet) {
        if (this.thunderListener != null) {
            thunderListener.handlePacketSend(packet);
        }
        if (packet.isCancelled()) {
            return;
        }
        this.channel.processOut(packet);
    }

    @SneakyThrows @Override
    public synchronized void flush() {
        this.channel.flush();
    }

    @Override @SneakyThrows
    public synchronized void sendPacket(Packet packet, ThunderClient client) {
        if (this.thunderListener != null) {
            thunderListener.handlePacketSend(packet);
        }
        if (packet.isCancelled()) {
            return;
        }
        this.channel.processOut(packet, client.getChannel());
    }

    @Override
    public synchronized void sendPacket(Packet packet, ThunderSession session) {
        if (this.thunderListener != null) {
            thunderListener.handlePacketSend(packet);
        }
        if (packet.isCancelled()) {
            return;
        }
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
        this.session.setHandShaked(false);
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

