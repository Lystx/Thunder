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
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.impl.object.ObjectHandler;
import io.thunder.packet.impl.object.PacketObject;
import io.thunder.utils.objects.ThunderAction;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Serializable;
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

    /**
     * The Socket of the Server
     */
    private ServerSocket server;

    /**
     * The connected Clients
     */
    private final List<ThunderClient> clients;

    /**
     * The PacketAdapter to handle Packets
     */
    private final PacketAdapter packetAdapter;

    /**
     * The session of this server
     */
    private final ThunderSession session;

    /**
     * The channel
     */
    private final ThunderChannel channel;

    /**
     * The default Encoder
     */
    private PacketEncoder encoder = new DefaultPacketEncoder();

    /**
     * The default Decoder
     */
    private PacketDecoder decoder = new DefaultPacketDecoder();

    /**
     * The default PreDecoder
     */
    private PacketPreDecoder preDecoder = new DefaultPacketPreDecoder();

    /**
     * The Listener to handle
     */
    private ThunderListener thunderListener;

    /**
     * The ObjectHandlers to handle Objects
     */
    private final List<ObjectHandler<?>> objectHandlers;

    /**
     * The PacketCompressors
     */
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
    public synchronized void sendObject(Serializable object) {

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

                                        thunderServer.sendPacket(packetHandshake, thunderSession, response -> {

                                            try {
                                                String sessionId = response.get(0).asString();
                                                UUID uuid = response.get(1).asUUID();
                                                long time = response.get(2).asLong();

                                                thunderSession.setSessionId(sessionId.split("ThunderSession#")[1]);
                                                thunderSession.setUniqueId(uuid);
                                                thunderSession.setStartTime(time);
                                                thunderSession.setHandShaked(true);

                                                session.getConnectedSessions().add(thunderSession);
                                            } catch (Exception e) {
                                                LOGGER.log(LogLevel.ERROR, "Coulnd't get Respond of HandShakePacket from ThunderConnection!");
                                                if (LOGGER.getLogLevel().equals(LogLevel.ERROR)) {
                                                    e.printStackTrace();
                                                }
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
                LOGGER.log(LogLevel.ERROR, "(Server-Side) Server couldn't start! :");
                if (LOGGER.getLogLevel().equals(LogLevel.ERROR)) {
                    Thunder.ERROR_HANDLER.onError(e);
                }
            }
        }, this);
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("-------------[ThunderServer]------------").append("\n");
        sb.append("Session-Name : " + session.getSessionId()).append("\n");
        sb.append("Session-UUID : " + session.getUniqueId()).append("\n");
        sb.append("Session-Start : " + session.getStartTime()).append("\n");
        sb.append("Session-Handshaked : " + session.isHandShaked()).append("\n");
        sb.append("--------------------").append("\n");
        sb.append("Connection-Type : " + "SERVER").append("\n");
        sb.append("Connection-Channel-RemoteAddress : " + this.channel.remoteAddress().toString()).append("\n");
        sb.append("Connection-Channel-LocalAddress : " + this.channel.localAddress().toString()).append("\n");
        sb.append("Connection-Channel-Valid : " + this.channel.isValid()).append("\n");
        sb.append("Connection-Channel-Opened : " + this.channel.isOpen()).append("\n");
        sb.append("--------------------").append("\n");
        sb.append("General-Listner : " + (this.getThunderListener() != null)).append("\n");
        sb.append("General-ObjectHandlers : " + this.getObjectHandlers().size()).append("\n");
        sb.append("--------------------").append("\n");
        sb.append("Codec-PreDecoder : " + preDecoder.getClass().getName()).append("\n");
        sb.append("Codec-Decoder : " + decoder.getClass().getName()).append("\n");
        sb.append("Codec-Encoder : " + encoder.getClass().getName()).append("\n");
        sb.append("-------------[ThunderServer]------------");

        return sb.toString();
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

