package io.thunder.impl.connection;

import io.thunder.Thunder;
import io.thunder.codec.PacketCodec;
import io.thunder.codec.PacketDecoder;
import io.thunder.codec.PacketEncoder;
import io.thunder.codec.PacketPreDecoder;
import io.thunder.codec.impl.DefaultPacketDecoder;
import io.thunder.codec.impl.DefaultPacketEncoder;
import io.thunder.codec.impl.DefaultPacketPreDecoder;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.connection.extra.ThunderSession;
import io.thunder.impl.channel.ClientThunderChannel;
import io.thunder.impl.other.ProvidedThunderAction;
import io.thunder.impl.other.ProvidedThunderSession;
import io.thunder.manager.factory.ThunderFactorySocket;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.handler.PacketAdapter;
import io.thunder.packet.object.ObjectHandler;
import io.thunder.packet.object.PacketObject;
import io.thunder.utils.ThunderAction;
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
public class ProvidedThunderClient implements ThunderClient {


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

    private ProvidedThunderClient(ThunderFactorySocket sf) {
        this.sf = sf;
        this.packetAdapter = new PacketAdapter();
        this.objectHandlers = new ArrayList<>();

        this.session = ProvidedThunderSession.newInstance("[t:" + System.currentTimeMillis() + ", j: " + System.getProperty("java.version") + "]", UUID.randomUUID(), new LinkedList<>(), System.currentTimeMillis(), this, null, false);
        this.channel = ClientThunderChannel.newInstance(this);
    }

    public static ProvidedThunderClient newInstance(ThunderFactorySocket sf) {
        return new ProvidedThunderClient(sf);
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
    public synchronized ThunderAction<ThunderClient> connect(String host, int port) {
        return ProvidedThunderAction.newInstance(thunderClient -> {

            if (socket != null && !socket.isClosed()) {
                throw new IllegalStateException("ThunderClient could not be opened because it's already connected!");
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
            throw new IllegalStateException("ThunderClient could not be opened because it's already connected!");
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
                    packet = this.preDecoder.decode(new PacketBuffer(dataInputStream));
                } catch (Exception e) {
                    disconnect();
                    break;
                }

                LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient received packet " + packet);

                if (thunderListener != null) {
                    try {
                        thunderListener.readPacket(packet, ProvidedThunderClient.this);
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
        if (!this.isConnected()) {
            return;
        }
        this.socket.getOutputStream().flush();
    }


    @Override
    public synchronized void sendPacket(Packet packet) {
        if (!this.isConnected()) {
            return;
        }
        LOGGER.log(LogLevel.DEBUG, "(Client-Side) ThunderClient sending packet " + packet);
        this.channel.processOut(packet);
    }

    @Override
    public synchronized void sendObject(Object object) {
        if (!this.isConnected()) {
            return;
        }
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
            this.encoder = (PacketEncoder) packetCodec;
        } else if (packetCodec instanceof PacketPreDecoder) {
            this.preDecoder = (PacketPreDecoder) packetCodec;
        } else {
            this.decoder = (PacketDecoder) packetCodec;
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
