package io.thunder;

import io.thunder.codec.PacketCodec;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.codec.PacketPreDecoder;
import io.thunder.codec.impl.DefaultPacketDecoder;
import io.thunder.codec.impl.DefaultPacketEncoder;
import io.thunder.codec.impl.DefaultPacketPreDecoder;
import io.thunder.connection.*;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.manager.factory.ThunderFactoryClient;
import io.thunder.manager.factory.ThunderFactoryServerSocket;
import io.thunder.manager.factory.ThunderFactorySocket;
import io.thunder.manager.logger.LogLevel;
import io.thunder.manager.logger.Logger;
import io.thunder.packet.*;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.object.ObjectHandler;
import io.thunder.packet.object.PacketObject;
import io.thunder.utils.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Thunder {

    public static final Logger LOGGER = new Logger(); //Custom logger for Thunder

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(); //ExecutorService to make things run async


    /**
     * Sets the level for the Logger
     * so you could disable it or only listen to
     * errors for example if you use {@link LogLevel#ERROR}
     *
     * @param logging the LogLevel
     */
    public synchronized static void setLogging(LogLevel logging) {
        LOGGER.setLogLevel(logging);
    }

    /**
     * Creates a new {@link ThunderClient}
     *
     * @return the created Client
     */
    public synchronized static ThunderClient createClient() {
        return createClient(new ThunderListener() {
            @Override
            public void handleConnect(ThunderSession session) {
            }

            @Override
            public void handleDisconnect(ThunderSession session) {
            }
        });
    }

    /**
     * Creates a new {@link ThunderClient}
     * and automatically sets the {@link ThunderListener}
     * for it but it not connects the Client
     *
     * @param thunderListener
     * @return the created Client
     */
    public synchronized static ThunderClient createClient(ThunderListener thunderListener) {
        ImplThunderClient implThunderClient = new ImplThunderClient(Socket::new);
        if (thunderListener != null) implThunderClient.addSessionListener(thunderListener);
        return implThunderClient;
    }


    /**
     * Creates a {@link ThunderServer} with
     * no Encryption at all it's a "raw" server
     * with no Listener
     *
     * @return created Server
     */
    public synchronized static ThunderServer createServer() {
        return createServer(new ThunderListener() {
            @Override
            public void handleConnect(ThunderSession session) {
            }

            @Override
            public void handleDisconnect(ThunderSession session) {
            }
        });
    }

    /**
     * Creates a {@link ThunderServer} and
     * sets the Listener for it automatically
     *
     * @param thunderListener the Listener
     * @return created Server
     */
    public synchronized static ThunderServer createServer(ThunderListener thunderListener) {
        ImplThunderServer implThunderServer = new ImplThunderServer(ServerSocket::new, () -> new ImplThunderClient(Socket::new));
        if (thunderListener != null) implThunderServer.addSessionListener(thunderListener);
        return implThunderServer;
    }


    @Getter
    private static class ImplThunderClient implements ThunderClient {


        private final ThunderFactorySocket sf;

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        private final PacketAdapter packetAdapter;
        private final ThunderSession session;
        private final ThunderChannel channel;


        private PacketEncoder encoder = new DefaultPacketEncoder();
        private PacketDecoder decoder = new DefaultPacketDecoder();
        private PacketPreDecoder preDecoder = new DefaultPacketPreDecoder();

        private ThunderListener thunderListener;
        private final List<ObjectHandler<?>> objectHandlers;

        public ImplThunderClient(ThunderFactorySocket sf) {
            this.sf = sf;
            this.packetAdapter = new PacketAdapter();
            this.objectHandlers = new ArrayList<>();

            this.session = new ImplThunderSession("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this,null, false);
            this.channel = new ClientThunderChannel(this);
      }


        @Override
        public synchronized void setName(String name) {
            this.session.setSessionId(name);
        }

        @Override
        public synchronized void addSessionListener(ThunderListener clientListener) {
            this.thunderListener = clientListener;
        }

        @Override
        public synchronized void addObjectListener(ObjectHandler<?> objectHandler) {
            this.objectHandlers.add(objectHandler);
        }

        @Override
        public synchronized ImplThunderAction<ThunderClient> connect(String host, int port) {
            return new ImplThunderAction<>(thunderClient -> {

                if (socket != null && !socket.isClosed()) {
                    throw new IllegalStateException("Client not closed");
                }
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient trying to connect " + host + ":" + port + "...");
                try {
                    this.setSocket(sf.getSocket(host, port));
                    LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient connected successfully to " + host + ":" + port + "!");
                } catch (Exception e) {
                    LOGGER.log(LogLevel.ERROR, "(Client-Side) ThunderClient wasn't able to connect to " + host + ":" + port + "!");
                }
            }, this);
        }

        @Override
        public synchronized void setSocket(Socket socket) throws IOException {
            if (this.socket != null && !this.socket.isClosed()) {
                throw new IllegalStateException("Client not closed");
            }

            this.socket = socket;
            this.session.setChannel(this.channel);
            this.session.setAuthenticated(true);
            socket.setKeepAlive(false);
            this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            Thunder.EXECUTOR_SERVICE.submit(() -> {

                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient-Listener-Thread starting...");
                while (this.isConnected()) {
                    Packet packet;
                    try {
                        packet = getPreDecoder().decode(new PacketBuffer(dataInputStream));
                    } catch (Exception e) {
                        disconnect();
                        break;
                    }

                    LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient received packet " + packet);

                    if (thunderListener != null) {
                        try {
                            thunderListener.readPacket(packet, ImplThunderClient.this);
                        } catch (Exception e) {
                            LOGGER.log(LogLevel.ERROR, "(Client-Side) ThunderClient was unable to handle packet " + packet);
                            e.printStackTrace();
                        }
                    }
                }
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient-Listener-Thread stopped!");
            });

            if (this.thunderListener != null) {
                this.thunderListener.handleConnect(this.session);
            }
        }

        @Override @SneakyThrows
        public synchronized void flush() {
            this.socket.getOutputStream().flush();
        }


        @Override
        public synchronized void sendPacket(Packet packet) {
            if (!isConnected()) return;
            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient sending packet " + packet);
            this.channel.processOut(packet);
        }

        @Override
        public synchronized void sendObject(Object object) {
            this.sendPacket(new PacketObject<>(object, System.currentTimeMillis()));
        }

        @Override
        public synchronized void disconnect() {
            if (socket == null || socket.isClosed()) {
                return;
            }

            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient stopping...");
            try {
                socket.close();
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient closed!");


                this.session.setAuthenticated(false);
                if (thunderListener != null) {
                    thunderListener.handleDisconnect(this.session);
                }

            } catch (final IOException e) {
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient couldn't be closed!");
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
            return socket != null && socket.isConnected() && !socket.isClosed();
        }

        @Override
        public synchronized String toString() {
            return asString();
        }
    }

    @Getter
    private static class ImplThunderServer implements ThunderServer {


        private final ThunderFactoryServerSocket ssf;
        private final ThunderFactoryClient cf;

        private ServerSocket server;
        private final List<ThunderClient> clients;
        private final PacketAdapter packetAdapter;
        private final ImplThunderSession session;
        private final ThunderChannel channel;

        private PacketEncoder encoder = new DefaultPacketEncoder();
        private PacketDecoder decoder = new DefaultPacketDecoder();
        private PacketPreDecoder preDecoder = new DefaultPacketPreDecoder();

        private ThunderListener thunderListener;
        private final List<ObjectHandler<?>> objectHandlers;


        public ImplThunderServer(ThunderFactoryServerSocket ssf, ThunderFactoryClient cf) {
            this.ssf = ssf;
            this.cf = cf;

            this.clients = new LinkedList<>();
            this.packetAdapter = new PacketAdapter();
            this.objectHandlers = new ArrayList<>();

            this.session = new ImplThunderSession("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this,null, false);
            this.channel = new ServerThunderChannel(this);

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
        public synchronized ImplThunderAction<ThunderServer> start(int port) {

            return new ImplThunderAction<>(thunderServer -> {
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
                                            thunderListener.readPacket(packet, ImplThunderServer.this);
                                        }
                                    }
                                });

                                thunderClient.setSocket(socket);
                            } catch (final SocketException e) {
                                this.disconnect();
                                break;
                            } catch (final IOException e) {
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
            } catch (final Exception e) {
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

    @Getter @AllArgsConstructor
    private static class ImplThunderSession implements ThunderSession {

        private String sessionId;
        private final UUID uniqueId;
        private final List<ThunderSession> connectedSessions;
        private final long startTime;
        private final ThunderConnection connection;

        @Setter
        private ThunderChannel channel;


        @Setter
        private boolean authenticated;

        public void setSessionId(String sessionId) {
            this.sessionId = "ThunderSession#" + sessionId;
        }

        public String getSessionId() {
            return "ThunderSession#" + sessionId;
        }

        /**
         * Returns a {@link ImplThunderSession} by its {@link UUID}
         *
         * @param uuid the UUID of the Session
         * @return the searched Session
         */
        public ThunderSession getSession(UUID uuid) {
            return this.connectedSessions.stream().filter(implThunderSession -> implThunderSession.getUniqueId().equals(uuid)).findFirst().orElse(null);
        }
    }

    @AllArgsConstructor
    public static class ImplThunderAction<T> implements ThunderAction<T> {

        private final Consumer<T> consumer;
        private final T t;


        public void perform() {
            this.perform(t -> {});
        }

        public void performAsync() {
            EXECUTOR_SERVICE.execute(this::perform);
        }

        public void performAsync(Consumer<T> consumer) {
            EXECUTOR_SERVICE.execute(() -> perform(consumer));
        }

        public void perform(Consumer<T> consumer) {
            this.consumer.accept(t);
            consumer.accept(this.t);
        }

        public T get() {
            return this.t;
        }
    }

    @AllArgsConstructor
    private static class ClientThunderChannel implements ThunderChannel {

        private final ThunderClient thunderClient;

        @Override @SneakyThrows
        public void processIn(Packet packet) {

            packet.setChannel(thunderClient.getChannel());
            packet.setConnection(this.thunderClient);

            ThunderConnection.processOut(packet, this.getOut());
        }

        @Override @SneakyThrows
        public void processOut(Packet packet) {

            packet.setChannel(this.thunderClient.getChannel());
            packet.setConnection(this.thunderClient);

            DataOutputStream out = this.getOut();
            ThunderConnection.processOut(packet, out);
            out.flush();
        }

        @Override
        public void processOut(Packet packet, ThunderChannel thunderChannel) {
            throw new UnsupportedOperationException("Not available for ThunderClient!");
        }

        @Override @SneakyThrows
        public void flush() {
            this.getOut().flush();
        }

        @Override @SneakyThrows
        public DataOutputStream getOut() {
            return new DataOutputStream(this.thunderClient.getSocket().getOutputStream());
        }

        @Override @SneakyThrows
        public DataInputStream getIn() {
            return new DataInputStream(this.thunderClient.getSocket().getInputStream());
        }

        @Override
        public SocketAddress remoteAddress() {
            return this.thunderClient.getSocket().getRemoteSocketAddress();
        }

        @Override
        public SocketAddress localAddress() {
            return this.thunderClient.getSocket().getLocalSocketAddress();
        }

        @Override
        public ThunderSession getSession() {
            return this.thunderClient.getSession();
        }

        @Override
        public boolean isOpen() {
            return this.thunderClient.isConnected();
        }

        @Override
        public void close() throws IOException {
            this.thunderClient.getSocket().close();
        }
    }

    @AllArgsConstructor
    private static class ServerThunderChannel implements ThunderChannel {

        private final ThunderServer thunderServer;

        @Override
        public void processIn(Packet packet) {
            throw new UnsupportedOperationException("Not available for ThunderServer!");
        }

        @Override @SneakyThrows
        public void processOut(Packet packet) {

            packet.setChannel(this.thunderServer.getChannel());
            packet.setConnection(this.thunderServer);

            for (ThunderClient client : this.thunderServer.getClients()) {
                ThunderConnection.processOut(
                        packet,
                        new DataOutputStream(
                                client
                                        .getSocket()
                                        .getOutputStream()
                        )
                );
            }
        }

        @Override @SneakyThrows
        public void processOut(Packet packet, ThunderChannel thunderChannel) {

            packet.setChannel(this.thunderServer.getChannel());
            packet.setConnection(this.thunderServer);

            DataOutputStream dataOutputStream = thunderChannel.getOut();
            ThunderConnection.processOut(packet, dataOutputStream);
        }

        @Override @SneakyThrows
        public void flush() {
            for (ThunderClient client : this.thunderServer.getClients()) {
                client.getSocket().getOutputStream().flush();
            }
        }

        @Override
        public DataOutputStream getOut() {
            throw new UnsupportedOperationException("Not available for ThunderServer!");
        }

        @Override
        public DataInputStream getIn() {
            throw new UnsupportedOperationException("Not available for ThunderServer!");
        }

        @Override
        public SocketAddress remoteAddress() {
            return new InetSocketAddress(this.thunderServer.getServer().getInetAddress().getHostName(), this.thunderServer.getServer().getLocalPort());
        }

        @Override
        public SocketAddress localAddress() {
            return this.thunderServer.getServer().getLocalSocketAddress();
        }

        @Override
         public ThunderSession getSession() {
             return this.thunderServer.getSession();
         }

        @Override
        public boolean isOpen() {
            return !this.thunderServer.getServer().isClosed();
        }

        @Override
        public void close() throws IOException {
            for (ThunderClient client : this.thunderServer.getClients()) {
                client.disconnect();
            }
            this.thunderServer.getServer().close();
        }
    }

}
