package io.thunder;

import io.thunder.codec.PacketCodec;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.codec.impl.DefaultPacketDecoder;
import io.thunder.codec.impl.DefaultPacketEncoder;
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
import io.thunder.utils.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Thunder {

    public static final Logger LOGGER = new Logger(); //Custom logger for Thunder

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(); //ExecutorService to make things run async
    @Getter
    private static boolean USE_COMPRESSOR = false;

    public static void useCompressor(boolean b) {
        USE_COMPRESSOR = b;
    }

    /**
     * Sets the level for the Logger
     * so you could disable it or only listen to
     * errors for example if you use {@link LogLevel#ERROR}
     *
     * @param logging the LogLevel
     */
    public static void setLogging(LogLevel logging) {
        LOGGER.setLogLevel(logging);
    }

    /**
     * Creates a new {@link ThunderClient}
     *
     * @return the created Client
     */
    public static ThunderClient createClient() {
        return createClient(new EmptyThunderListener());
    }

    /**
     * Creates a new {@link ThunderClient}
     * and automatically sets the {@link ThunderListener}
     * for it but it not connects the Client
     *
     * @param thunderListener
     * @return the created Client
     */
    public static ThunderClient createClient(ThunderListener thunderListener) {
        ImplThunderClient implThunderClient = new ImplThunderClient(Socket::new);
        if (thunderListener != null) implThunderClient.addHandler(thunderListener);
        return implThunderClient;
    }


    /**
     * Creates a {@link ThunderServer} with
     * no Encryption at all it's a "raw" server
     * with no Listener
     *
     * @return created Server
     */
    public static ThunderServer createServer() {
        return createServer(new EmptyThunderListener());
    }

    /**
     * Creates a {@link ThunderServer} and
     * sets the Listener for it automatically
     *
     * @param thunderListener the Listener
     * @return created Server
     */
    public static ThunderServer createServer(ThunderListener thunderListener) {
        ImplThunderServer implThunderServer = new ImplThunderServer(ServerSocket::new, () -> new ImplThunderClient(Socket::new));
        if (thunderListener != null) implThunderServer.addHandler(thunderListener);
        return implThunderServer;
    }


    @Getter
    private static class ImplThunderClient implements ThunderClient {


        private final ThunderFactorySocket sf;

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        private ThunderListener clientListener;
        private final PacketAdapter packetAdapter;
        private final ThunderSession session;
        private final ThunderChannel channel;


        private PacketEncoder encoder = new DefaultPacketEncoder();
        private PacketDecoder decoder = new DefaultPacketDecoder();


        public ImplThunderClient(ThunderFactorySocket sf) {
            this.sf = sf;
            this.packetAdapter = new PacketAdapter();

            this.session = new ImplThunderSession("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), null, false);
            this.channel = new ClientThunderChannel(this);
      }


        @Override
        public void setName(String name) {
            this.session.setSessionId(name);
        }

        @Override
        public synchronized void addHandler(ThunderListener clientListener) {
            this.clientListener = clientListener;
        }

        @Override
        public synchronized ImplThunderAction<ThunderClient> connect(String host, int port) {
            return new ImplThunderAction<>(thunderClient -> {

                if (socket != null && !socket.isClosed()) throw new IllegalStateException("Client not closed");
                if (host.isEmpty() || port == -1) throw new IllegalStateException("Host and port are not set");

                LOGGER.log(LogLevel.INFO, "Client connecting to " + host + ":" + port + "...");
                try {
                    this.setSocket(sf.getSocket(host, port));
                    LOGGER.log(LogLevel.DEBUG, "Client connected successfully to " + host + ":" + port + "!");
                } catch (Exception e) {
                    LOGGER.log(LogLevel.ERROR, "Client was Unable to connect to " + host + ":" + port + "!");
                }
            }, this);
        }

        @Override
        public synchronized void setSocket(Socket socket) throws IOException {
            if (this.socket != null && !this.socket.isClosed()) throw new IllegalStateException("Client not closed");

            this.socket = socket;
            this.session.setChannel(this.channel);
            this.session.setAuthenticated(true);
            socket.setKeepAlive(false);
            this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            LOGGER.log(LogLevel.DEBUG, "Client-Listener-Thread starting...");
            Thunder.EXECUTOR_SERVICE.submit(this::listenerThreadImpl);

            if (this.clientListener != null) this.clientListener.handleConnect(this);
        }

        @Override @SneakyThrows
        public void flush() {
            this.socket.getOutputStream().flush();
        }

        private void listenerThreadImpl() {
            while (true) {
                Packet packet;

                try {

                    long time = dataInputStream.readLong();
                    int protocolId = dataInputStream.readInt();
                    int protocolVersion = dataInputStream.readInt();
                    UUID uniqueId = new UUID(dataInputStream.readLong(), dataInputStream.readLong());
                    int dataLength = dataInputStream.readInt();
                    byte[] data = new byte[dataLength]; dataInputStream.readFully(data);


                    packet = new Packet() {
                        @Override
                        public void write(PacketBuffer buf) {}

                        @Override
                        public void read(PacketBuffer buf) {}
                    };


                    packet.setData(data);

                    packet.setProcessingTime(time);
                    packet.setProtocolId(protocolId);
                    packet.setProtocolVersion(protocolVersion);
                    packet.setUniqueId(uniqueId);

                } catch (SocketException | EOFException e) {
                    disconnect();
                    break;
                } catch (IOException e) {
                    LOGGER.log(LogLevel.ERROR, "Error in Client-Listener-Thread! " + e.getCause());
                    disconnect();
                    break;
                }
                LOGGER.log(LogLevel.DEBUG, "Client received packet " + packet);

                if (clientListener != null) {
                    try {
                        clientListener.readPacket(packet, this);
                    } catch (IOException e) {
                        LOGGER.log(LogLevel.WARNING, "Client was unable to handle packet " + packet);
                    } catch (Exception e) {
                        LOGGER.log(LogLevel.ERROR, "Exception while handling packet " + packet + ":");
                        e.printStackTrace();
                    }
                }
            }

            LOGGER.log(LogLevel.DEBUG, "Client-Listener-Thread stopped!");
        }

        @Override
        public synchronized void sendPacket(Packet packet) {
            if (!isConnected()) return;
            LOGGER.log(LogLevel.DEBUG, "Client sending packet " + packet);
            this.channel.processOut(packet);
        }

        @Override
        public synchronized void disconnect() {
            if (socket == null) return;
            if (socket.isClosed()) return;

            LOGGER.log(LogLevel.INFO, "Client stopping...");

            try {
                socket.close();
                LOGGER.log(LogLevel.DEBUG, "Client closed!");
            } catch (final IOException e) {
                LOGGER.log(LogLevel.DEBUG, "Client couldn't be closed!");
            }

            this.session.setAuthenticated(false);
            if (clientListener != null) clientListener.handleDisconnect(this);
        }


        @Override
        public void addCodec(PacketCodec packetCodec) {
            if (packetCodec instanceof PacketEncoder) {
                encoder = (PacketEncoder) packetCodec;
            } else {
                decoder = (PacketDecoder) packetCodec;
            }
        }

        @Override
        public synchronized boolean isConnected() {
            return socket != null && socket.isConnected() && !socket.isClosed();
        }

        @Override
        public synchronized Socket getSocket() {
            return socket;
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
        private ThunderListener serverListener;
        private final PacketAdapter packetAdapter;
        private final ImplThunderSession session;
        private final ThunderChannel channel;

        private PacketEncoder encoder = new DefaultPacketEncoder();
        private PacketDecoder decoder = new DefaultPacketDecoder();


        public ImplThunderServer(ThunderFactoryServerSocket ssf, ThunderFactoryClient cf) {
            this.ssf = ssf;
            this.cf = cf;

            this.clients = new LinkedList<>();
            this.packetAdapter = new PacketAdapter();

            this.session = new ImplThunderSession("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), null, false);
            this.channel = new ServerThunderChannel(this);

        }

        @Override
        public void setName(String name) {
            this.session.setSessionId(name);
        }

        @Override
        public synchronized void addHandler(ThunderListener serverListener) {
            this.serverListener = serverListener;
        }

        @Override
        public synchronized ImplThunderAction<ThunderServer> start(int port) {

            return new ImplThunderAction<>(thunderServer -> {
                LOGGER.log(LogLevel.DEBUG, "Server starting...");

                try {
                    server = ssf.getSocket(port);
                    this.session.setAuthenticated(true);
                    this.session.setChannel(this.channel);


                    LOGGER.log(LogLevel.DEBUG, "Server-Listener-Thread started!");
                    Thunder.EXECUTOR_SERVICE.submit(this::acceptorThreadImpl);
                } catch (Exception e) {
                    if (LOGGER.getLogLevel().equals(LogLevel.ERROR) || LOGGER.getLogLevel().equals(LogLevel.ALL)) {
                        LOGGER.log(LogLevel.ERROR, "Server couldn't start! :");
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
        public void sendPacket(Packet packet) {
            this.channel.processOut(packet);
        }

        @SneakyThrows @Override
        public void flush() {
            this.channel.flush();
        }

        @Override @SneakyThrows
        public void sendPacket(Packet packet, ThunderClient client) {
            this.channel.processOut(packet, client.getChannel());
        }

        @Override
        public void sendPacket(Packet packet, ThunderSession session) {
            ThunderClient thunderClient = this.clients.stream().filter(thunderClient1 -> thunderClient1.getSession().getUniqueId().equals(session.getUniqueId())).findFirst().orElse(null);
            if (thunderClient == null) {
                return;
            }
            this.sendPacket(packet, thunderClient);
        }

        private void acceptorThreadImpl() {
            while (true) {
                try {
                    final Socket socket = server.accept();
                    final ThunderClient thunderClient = cf.getThunderClient();
                    this.session.getConnectedSessions().add(thunderClient.getSession()); //TODO: CHECK

                    thunderClient.addHandler(new ThunderListener() {
                        @Override
                        public void handleConnect(ThunderConnection thunderClient) {
                            synchronized (clients) {
                                LOGGER.log(LogLevel.DEBUG, "Client " + thunderClient.toString() + " has connected!");
                                clients.add((ThunderClient) thunderClient);
                            }
                            if (serverListener != null) serverListener.handleConnect(thunderClient);
                        }

                        @Override
                        public void handleDisconnect(ThunderConnection thunderClient) {
                            synchronized (clients) {
                                LOGGER.log(LogLevel.DEBUG, "Client " + thunderClient.toString() + " has disconnected!");
                                clients.remove((ThunderClient) thunderClient);
                            }
                            session.getConnectedSessions().remove(session.getSession(thunderClient.getSession().getUniqueId()));
                            if (serverListener != null) serverListener.handleDisconnect(thunderClient);
                        }

                        @Override
                        public void handlePacket(Packet packet, ThunderConnection thunderClient) throws IOException {

                        }

                        @Override
                        public void readPacket(Packet packet, ThunderConnection thunderClient) throws IOException {
                            if (serverListener != null) {
                                serverListener.readPacket(packet, ImplThunderServer.this);
                            }
                        }
                    });

                    thunderClient.setSocket(socket);
                } catch (final SocketException e) {
                    disconnect();
                    break;
                } catch (final IOException e) {
                    LOGGER.log(LogLevel.ERROR, "Error in Server-Listener-Thread!");
                    disconnect();
                    break;
                }
            }

            LOGGER.log(LogLevel.DEBUG, "Server-Listener-Thread stopped!");
        }

        @Override
        public synchronized void disconnect() {

            LOGGER.log(LogLevel.INFO, "Server stopping...");

            synchronized (clients) {
                for (ThunderClient thunderClient : clients) {
                    thunderClient.addHandler(null);
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
                LOGGER.log(LogLevel.DEBUG, "Server closed!");
            } catch (final Exception e) {
                LOGGER.log(LogLevel.ERROR, "Server couldn't be closed!");
            }
        }

        @Override
        public void addCodec(PacketCodec packetCodec) {
            if (packetCodec instanceof PacketEncoder) {
                encoder = (PacketEncoder) packetCodec;
            } else {
                decoder = (PacketDecoder) packetCodec;
            }
        }

        @Override
        public boolean isConnected() {
            return true;
        }

    }

    @Getter @AllArgsConstructor
    private static class ImplThunderSession implements ThunderSession {

        private String sessionId;
        private final UUID uniqueId;
        private final List<ThunderSession> connectedSessions;
        private final long startTime;

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

    private static class EmptyThunderListener implements ThunderListener {
        @Override
        public void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
        }
        @Override
        public void handleConnect(ThunderConnection thunderConnection) {
        }
        @Override
        public void handleDisconnect(ThunderConnection thunderConnection) {
        }
    }

    @AllArgsConstructor
    private static class ClientThunderChannel implements ThunderChannel {

        private final ThunderClient thunderClient;

        @Override @SneakyThrows
        public void processIn(Packet packet) {
            packet.setConnection(this.thunderClient);
            ThunderConnection.processOut(packet, this.getOut());
        }

        @Override @SneakyThrows
        public void processOut(Packet packet) {
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
            packet.setConnection(this.thunderServer);
            for (ThunderClient client : this.thunderServer.getClients()) {
                ThunderConnection.processOut(packet, new DataOutputStream(client.getSocket().getOutputStream()));
            }
        }

        @Override @SneakyThrows
        public void processOut(Packet packet, ThunderChannel thunderChannel) {
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
