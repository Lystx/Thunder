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
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.base.ThunderSession;
import io.thunder.impl.channel.ClientThunderChannel;
import io.thunder.impl.other.ProvidedThunderAction;
import io.thunder.impl.other.ProvidedThunderSession;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.impl.other.ProvidedPacketBuffer;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.impl.object.ObjectHandler;
import io.thunder.packet.impl.object.PacketObject;
import io.thunder.utils.objects.ThunderAction;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.thunder.Thunder.LOGGER;

@Getter
public class ProvidedThunderClient implements ThunderClient, PacketHandler {

    /**
     * The socket of the Client
     */
    private Socket socket;

    /**
     * The dataInput to receive data
     */
    private DataInputStream dataInputStream;

    /**
     * The dataOutput to send data
     */
    private DataOutputStream dataOutputStream;

    /**
     * The packetAdapter to handle Packets
     */
    private final PacketAdapter packetAdapter;

    /**
     * The session of this client
     */
    private final ThunderSession session;

    /**
     * The channel of this client
     */
    private final ThunderChannel channel;

    /**
     * The default {@link PacketEncoder}
     */
    private PacketEncoder encoder = new DefaultPacketEncoder();

    /**
     * The default {@link PacketDecoder}
     */
    private PacketDecoder decoder = new DefaultPacketDecoder();

    /**
     * The default {@link PacketPreDecoder}
     */
    private PacketPreDecoder preDecoder = new DefaultPacketPreDecoder();

    /**
     * The thunderlistener to handle connect and disconnect
     */
    private ThunderListener thunderListener;

    /**
     * The ObjectHandlers to handle incoming objects
     */
    private final List<ObjectHandler<?>> objectHandlers;

    /**
     * The ObjectHandlers to handle incoming objects
     */
    private final List<PacketCompressor> packetCompressors;

    /**
     * Createing new {@link ThunderClient}
     */
    private ProvidedThunderClient() {
        this.packetAdapter = new PacketAdapter();
        this.objectHandlers = new ArrayList<>();
        this.packetCompressors = new ArrayList<>();

        this.session = ProvidedThunderSession.newInstance("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this, null, false);
        this.channel = ClientThunderChannel.newInstance(this);

        this.addPacketHandler(this);
    }

    /**
     * Creates a new {@link ThunderClient} with a given Factory
     * @return Thunderclient
     */
    public static ProvidedThunderClient newInstance() {
        return new ProvidedThunderClient();
    }

    /**
     * Sets the name of this connection
     * @param name the name of the conneciton
     */
    @Override
    public synchronized void setName(String name) {
        this.session.setSessionId(name);
    }

    /**
     * Adds a Listener to this session
     *
     * @param clientListener the listener
     */
    @Override
    public synchronized void addSessionListener(ThunderListener clientListener) {
        this.thunderListener = clientListener;
    }

    /**
     * Adds an {@link ObjectHandler} to this client
     *
     * @param objectHandler the handler to add
     */
    @Override
    public synchronized void addObjectListener(ObjectHandler<?> objectHandler) {
        this.objectHandlers.add(objectHandler);
    }

    @Override
    public synchronized void addCompressor(PacketCompressor compressor) {
        this.packetCompressors.add(compressor);
    }
    /**
     * Connects to the {@link io.thunder.connection.base.ThunderServer}
     *
     * @param host the host to connect to
     * @param port the port connect to
     * @return ThunderAction to perform
     */
    @Override
    public synchronized ThunderAction<ThunderClient> connect(String host, int port) {
        return ProvidedThunderAction.newInstance(thunderClient -> {

            if (socket != null && !socket.isClosed()) {
                throw new IllegalStateException("ThunderClient could not be opened because it's already connected!");
            }
            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient trying to connect " + host + ":" + port + "...");
            try {
                this.setSocket(new Socket(host, port));
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient connected successfully to " + host + ":" + port + "!");
            } catch (Exception e) {
                LOGGER.log(LogLevel.ERROR, "(Client-Side) ThunderClient wasn't able to connect to " + host + ":" + port + "!");
                if (LOGGER.getLogLevel().equals(LogLevel.ERROR)) {
                    Thunder.ERROR_HANDLER.onError(e);
                }
            }
        }, this);
    }

    /**
     * Sets the Socket of this {@link ThunderClient}
     *
     * @param socket the socket
     * @throws IOException if something goes wrong
     */
    @Override
    public synchronized void setSocket(Socket socket) throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            throw new IllegalStateException("ThunderClient could not be opened because it's already connected!");
        }

        //Socket managing
        socket.setKeepAlive(false);
        this.socket = socket;

        //Setting the session
        this.session.setChannel(this.channel);

        //Setting the Streams for In and OutPut
        this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        //Starting Thread for reading Packets
        Thunder.EXECUTOR_SERVICE.submit(() -> {

            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient-Listener-Thread starting...");
            while (this.isConnected()) {
                Packet packet;
                try {
                    //PreDecoding packet to get a raw object
                    packet = this.preDecoder.decode(ProvidedPacketBuffer.newInstance(dataInputStream));
                } catch (Exception e) {
                    //Couldn't encode this will break everything so disconnect!
                    this.disconnect();
                    break;
                }

                //Packet was fully read and nothing went wrong
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient received packet " + packet);

                if (thunderListener != null) {
                    try {
                        //Handling the packet via
                        thunderListener.readPacket(packet, ProvidedThunderClient.this);
                    } catch (Exception e) {
                        LOGGER.log(LogLevel.ERROR, "(Client-Side) ThunderClient was unable to handle packet " + packet.getClass().getSimpleName());
                        LOGGER.log(LogLevel.ERROR, "Exception: ");
                        Thunder.ERROR_HANDLER.onError(e);
                    }
                }
            }
            //Thread stopped
            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient-Listener-Thread stopped!");
        });

        if (this.thunderListener != null) {
            this.thunderListener.handleConnect(this.session);
        }
    }

    /**
     * Flushes this client
     */
    @Override @SneakyThrows
    public synchronized void flush() {
        this.channel.flush();
    }

    /**
     * Sends a packet through the {@link ThunderChannel}
     *
     * @param packet the packet to send
     */
    @Override
    public synchronized void sendPacket(Packet packet) {
        if (!this.isConnected()) {
            //If no connection no packet can be send
            return;
        }

        if (this.thunderListener != null) {
            thunderListener.handlePacketSend(packet);
        }
        if (packet.isCancelled()) {
            return;
        }

        LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient sending packet " + packet);
        this.channel.processOut(packet);
    }

    /**
     * Sends an Object to the {@link io.thunder.connection.base.ThunderServer}
     * @param object the objects
     */
    @Override
    public synchronized void sendObject(Serializable object) {
        if (!this.isConnected()) {
            return;
        }
        Serializable.class.isAssignableFrom(object.getClass());
        this.sendPacket(new PacketObject<>(object, System.currentTimeMillis()));
    }

    /**
     * Disconnects from the {@link io.thunder.connection.base.ThunderServer}
     * and closes the connection and session
     */
    @Override
    public synchronized void disconnect() {
        if (!channel.isValid() || !channel.isOpen()) {
            LOGGER.log(LogLevel.ERROR, "(Client-Side) ThunderClient tried to disconnect but was never connected");
            return;
        }
        LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient stopping...");
        try {
            channel.close(); //Closes the connection
            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient closed!");

            if (thunderListener != null) {
                thunderListener.handleDisconnect(this.session);
            }

            //Resets the session
            session.setHandShaked(false);
            session.setChannel(null);
            session.setSessionId(null);

        } catch (IOException e) {
            //Couldnt stop
            try {
                channel.close(); //Just trying one more time
            } catch (IOException ioException) {
                //Also didnt work
                LOGGER.log(LogLevel.DEBUG, "(Client-Side) Tried to close ThunderClient again but didnt work 2x!");
            }
            LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient couldn't be closed!");
        }
    }

    /**
     * Adds a {@link PacketCodec} to this Client
     * Can be a {@link PacketDecoder} | {@link PacketPreDecoder} | {@link PacketEncoder}
     *
     * @param packetCodec the PacketCodec
     */
    @Override
    public synchronized void addCodec(PacketCodec packetCodec) {
        if (packetCodec instanceof PacketEncoder) {
            this.encoder = (PacketEncoder) packetCodec;
        } else if (packetCodec instanceof PacketPreDecoder) {
            this.preDecoder = (PacketPreDecoder) packetCodec;
        } else {
            this.decoder = (PacketDecoder) packetCodec;
        }
    }

    /**
     * Checks if the {@link ThunderClient} is connected
     *
     * @return boolean
     */
    @Override
    public synchronized boolean isConnected() {
        return channel.isValid() && channel.isOpen();
    }

    /**
     * Transfers the data to string
     *
     * @return data as String
     */
    @Override
    public synchronized String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("-------------[ThunderClient]------------").append("\n");
        sb.append("Session-Name : " + session.getSessionId()).append("\n");
        sb.append("Session-UUID : " + session.getUniqueId()).append("\n");
        sb.append("Session-Start : " + session.getStartTime()).append("\n");
        sb.append("Session-Handshaked : " + session.isHandShaked()).append("\n");
        sb.append("--------------------").append("\n");
        sb.append("Connection-Type : " + "CLIENT").append("\n");
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
        sb.append("-------------[ThunderClient]------------");

        return sb.toString();
    }

    /**
     * Checks for the Handshake
     * @param packet the given Packet
     */
    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketHandshake) {
            try {
                PacketHandshake handshake = (PacketHandshake)packet;
                packet.respond(ResponseStatus.SUCCESS, this.session.getSessionId(), this.session.getUniqueId(), this.session.getStartTime());

                this.session.setHandShaked(true);
                if (this.thunderListener != null) {
                    this.thunderListener.handleHandshake(handshake);
                }
            } catch (Exception e) {
                LOGGER.log(LogLevel.ERROR, "Couldn't respond to Handshake because of:");
                if (LOGGER.getLogLevel().equals(LogLevel.ERROR)) {
                    e.printStackTrace();
                }
            }
        }
    }
}
