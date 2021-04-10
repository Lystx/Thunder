package io.thunder;

import com.google.gson.Gson;
import io.thunder.connection.ThunderClient;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.ThunderServer;
import io.thunder.connection.ThunderListener;
import io.thunder.connection.handler.ThunderPacketHandlerQuery;
import io.thunder.manager.factory.ClientFactory;
import io.thunder.manager.factory.ServerSocketFactory;
import io.thunder.manager.factory.SocketFactory;
import io.thunder.manager.packet.*;
import io.thunder.manager.packet.featured.Query;
import io.thunder.manager.packet.featured.QueryPacket;
import io.thunder.manager.packet.handler.PacketAdapter;
import io.thunder.manager.packet.handler.PacketHandler;
import io.thunder.manager.tls.TLSBuilder;
import io.thunder.manager.utils.LogLevel;
import io.thunder.manager.utils.Logger;
import io.thunder.manager.utils.PacketCompressor;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Thunder {

    public static final Logger LOGGER = new Logger(); //Custom logger for Thunder
    public static final Gson GSON = new Gson(); //Gson constant
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(); //ExecutorService to make things run async
    public static Boolean USE_COMPRESSOR = false;


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
        return createClient(null);
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
     * Creates a safe {@link ThunderClient} with
     * a CIPHER Encryption and uses TLS
     *
     * @param keyStore the stored key
     * @param keyStorePassword the stored password
     * @param keyStoreType the type
     *
     * @return the created Client
     */
    public static ThunderClient createClientTLS(byte[] keyStore, char[] keyStorePassword, String keyStoreType) {
        return new TLSThunderClient(keyStore, keyStorePassword, keyStoreType);
    }

    /**
     * Creates a safe {@link ThunderServer} with
     * a CIPHER Encryption and uses TLS
     *
     * @param keyStore the stored key
     * @param keyStorePassword the stored password
     * @param keyStoreType the type
     *
     * @return the created Server
     */
    public static ThunderServer createServerTLS(byte[] keyStore, char[] keyStorePassword, String keyStoreType) {
        return new TLSThunderServer(keyStore, keyStorePassword, keyStoreType);
    }

    /**
     * Creates a {@link ThunderServer} with
     * no Encryption at all it's a "raw" server
     * with no Listener
     *
     * @return created Server
     */
    public static ThunderServer createServer() {
        return createServer(null);
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


        private final SocketFactory sf;

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;


        private ThunderListener clientListener;
        private final UUID uniqueId;
        private final PacketAdapter packetAdapter;

        public ImplThunderClient(SocketFactory sf) {
            this.sf = sf;
            this.uniqueId = UUID.randomUUID();
            this.packetAdapter = new PacketAdapter();
            this.packetAdapter.addHandler(new ThunderPacketHandlerQuery(this));
        }

        @Override
        public synchronized void addHandler(final ThunderListener clientListener) {
            this.clientListener = clientListener;
        }

        @Override
        public synchronized void connect(final String host, final int port) {
            if (socket != null && !socket.isClosed()) throw new IllegalStateException("Client not closed");
            if (host.isEmpty() || port == -1) throw new IllegalStateException("Host and port are not set");

            LOGGER.log(LogLevel.INFO, "Client connecting to " + host + ":" + port + "...");
            try {
                setSocket(sf.getSocket(host, port));
                LOGGER.log(LogLevel.DEBUG, "Client connected successfully to " + host + ":" + port + "!");
            } catch (final Exception e) {
                LOGGER.log(LogLevel.ERROR, "Client was Unable to connect to " + host + ":" + port + "!");
            }
        }

        @Override
        public synchronized void setSocket(final Socket socket) throws IOException {
            if (this.socket != null && !this.socket.isClosed()) throw new IllegalStateException("Client not closed");

            this.socket = socket;
            socket.setKeepAlive(false);
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            LOGGER.log(LogLevel.DEBUG, "Client-Listener-Thread starting...");
            Thunder.EXECUTOR_SERVICE.submit(this::listenerThreadImpl);

            if (clientListener != null) clientListener.handleConnect(this);
        }

        private void listenerThreadImpl() {
            while (true) {
                Packet packet;

                try {
                    if (Thunder.USE_COMPRESSOR) {
                        packet = PacketCompressor.decompress(PacketReader.fromStream(dataInputStream));
                    } else {
                        packet = PacketReader.fromStream(dataInputStream);
                    }
                } catch (SocketException | EOFException e) {
                    disconnect();
                    break;
                } catch (IOException e) {
                    LOGGER.log(LogLevel.ERROR, "Error in Client-Listener-Thread! " + e.getCause());
                    disconnect();
                    break;
                }
                LOGGER.log(LogLevel.DEBUG, "Client received packet " + packet.toString());

                if (clientListener != null) {
                    try {
                        clientListener.handlePacket(packet, this);
                    } catch (IOException e) {
                        LOGGER.log(LogLevel.WARNING, "Client was unable to handle packet " + packet.toString());
                    } catch (Exception e) {
                        LOGGER.log(LogLevel.ERROR, "Exception while handling packet " + packet.toString() + ":");
                        e.printStackTrace();
                    }
                }
            }

            LOGGER.log(LogLevel.DEBUG, "Client-Listener-Thread stopped!");
        }

        @Override
        public synchronized void sendPacket(Packet packet) {
            if (!isConnected()) return;

            try {
                LOGGER.log(LogLevel.DEBUG, "Client sending packet " + packet);
                packet.write(dataOutputStream);
                dataOutputStream.flush();
            } catch (final IOException e) {
                LOGGER.log(LogLevel.DEBUG, "Client couldn't send packet " + packet);
            }
        }

        @Override
        public Query sendQuery(QueryPacket packet) {
            final Query[] query = {null};
            UUID uuid = UUID.randomUUID();
            packet.setUniqueId(uuid);

            this.getPacketAdapter().addHandler(new PacketHandler() {
                @Override
                public void handle(ThunderPacket packet) {
                    if (packet instanceof QueryPacket) {
                        QueryPacket resultPacket = (QueryPacket)packet;
                        if (uuid.equals(resultPacket.getUniqueId())) {
                            query[0] = resultPacket.getQuery();
                            getPacketAdapter().removeHandler(this);
                        }
                    }
                }
            });

            this.sendPacket(packet);
            int count = 0;

            while (query[0] == null && count++ < 3000) {
                try {
                    Thread.sleep(0, 500000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            if (count >= 2999) {
                Query r = new Query(null);
                query[0] = r;
            }
            return query[0];
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

            if (clientListener != null) clientListener.handleDisconnect(this);
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
            return socket.toString();
        }
    }

    @Getter
    private static class ImplThunderServer implements ThunderServer {


        private final ServerSocketFactory ssf;
        private final ClientFactory cf;

        private ServerSocket server;
        private final List<ThunderClient> thunderClients;
        private ThunderListener serverListener;
        private final UUID uniqueId;
        private final PacketAdapter packetAdapter;

        public ImplThunderServer(ServerSocketFactory ssf, ClientFactory cf) {
            this.ssf = ssf;
            this.cf = cf;

            this.thunderClients = new LinkedList<>();
            this.uniqueId = UUID.randomUUID();
            this.packetAdapter = new PacketAdapter();
            this.packetAdapter.addHandler(new ThunderPacketHandlerQuery(this));
        }

        @Override
        public synchronized void addHandler(ThunderListener serverListener) {
            this.serverListener = serverListener;
        }

        @Override
        public synchronized void start(int port) {
            LOGGER.log(LogLevel.DEBUG, "Server starting...");

            try {
                server = ssf.getServerSocket(port);
            } catch (final Exception e) {
                LOGGER.log(LogLevel.ERROR, "Server couldn't start! " + Arrays.toString(e.getStackTrace()));
                return;
            }

            LOGGER.log(LogLevel.DEBUG, "Server-Listener-Thread started!");
            Thunder.EXECUTOR_SERVICE.submit(this::acceptorThreadImpl);

        }

        @Override
        public void sendPacket(Packet packet) {
            for (ThunderClient thunderClient : this.thunderClients) {
                this.sendPacket(packet, thunderClient);
            }
        }


        @Override
        public Query sendQuery(QueryPacket packet) {
            final Query[] query = {null};
            UUID uuid = UUID.randomUUID();
            packet.setUniqueId(uuid);

            this.getPacketAdapter().addHandler(new PacketHandler() {
                @Override
                public void handle(ThunderPacket packet) {
                    if (packet instanceof QueryPacket) {
                        QueryPacket resultPacket = (QueryPacket)packet;
                        if (uuid.equals(resultPacket.getUniqueId())) {
                            query[0] = resultPacket.getQuery();
                            getPacketAdapter().removeHandler(this);
                        }
                    }
                }
            });

            this.sendPacket(packet);
            int count = 0;

            while (query[0] == null && count++ < 3000) {
                try {
                    Thread.sleep(0, 500000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            if (count >= 2999) {
                Query r = new Query(null);
                query[0] = r;
            }
            return query[0];
        }

        @Override @SneakyThrows
        public void sendPacket(Packet packet, ThunderClient client) {

            if (Thunder.USE_COMPRESSOR) {
                packet = PacketCompressor.compress(packet);
            }
            DataOutputStream dataOutputStream = new DataOutputStream(client.getSocket().getOutputStream());
            packet.write(dataOutputStream);
        }

        private void acceptorThreadImpl() {
            while (true) {
                try {
                    final Socket socket = server.accept();
                    final ThunderClient thunderClient = cf.getClient();

                    thunderClient.addHandler(new ThunderListener() {
                        @Override
                        public void handleConnect(ThunderConnection thunderClient) {
                            synchronized (thunderClients) {
                                LOGGER.log(LogLevel.DEBUG, "Client " + thunderClient.toString() + " has connected!");
                                thunderClients.add((ThunderClient) thunderClient);
                            }
                            if (serverListener != null) serverListener.handleConnect(thunderClient);
                        }

                        @Override
                        public void handleDisconnect(ThunderConnection thunderClient) {
                            synchronized (thunderClients) {
                                LOGGER.log(LogLevel.DEBUG, "Client " + thunderClient.toString() + " has disconnected!");
                                thunderClients.remove((ThunderClient) thunderClient);
                            }
                            if (serverListener != null) serverListener.handleDisconnect(thunderClient);
                        }

                        @Override
                        public void handlePacket(ThunderPacket packet, ThunderConnection thunderClient) throws IOException {

                        }

                        @Override
                        public void read(Packet packet, ThunderConnection thunderConnection) throws IOException {

                        }

                        @Override
                        public void handlePacket(Packet packet, ThunderConnection thunderClient) throws IOException {
                            if (serverListener != null) {
                                serverListener.handlePacket(packet, thunderClient);
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

            synchronized (thunderClients) {
                for (ThunderClient thunderClient : thunderClients) {
                    thunderClient.addHandler(null);
                    thunderClient.disconnect();
                }
                thunderClients.clear();
            }

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
        public boolean isConnected() {
            return true;
        }
    }

    private static class TLSThunderClient extends ImplThunderClient {

        private SSLSocket sslSocket;

        public TLSThunderClient() {
            super((host, port) ->
                    new TLSBuilder()
                            .port(host)
                            .port(port)
                            .buildSSLSocket()
            );
        }


        public TLSThunderClient(final byte[] trustStore, final char[] trustStorePassword, final String trustStoreType) {
            super((host, port) ->
                    new TLSBuilder()
                            .port(host)
                            .port(port)
                            .addTrustStore(trustStoreType, new ByteArrayInputStream(trustStore), trustStorePassword)
                            .buildSSLSocket()
            );
        }

        @Override
        public synchronized void setSocket(final Socket socket) throws IOException {
            super.setSocket(socket);
            sslSocket = (SSLSocket) socket;
        }

        public synchronized SSLSocket getSSLSocket() {
            return sslSocket;
        }
    }

    private static class TLSThunderServer extends ImplThunderServer {

        public TLSThunderServer(byte[] keyStore, char[] keyStorePassword, String keyStoreType) {
            super(port -> new TLSBuilder()
                            .port(port)
                            .addKeyStore(keyStoreType, new ByteArrayInputStream(keyStore), keyStorePassword)
                            .buildSSLServer(),
                    TLSThunderClient::new);
        }
    }
}
