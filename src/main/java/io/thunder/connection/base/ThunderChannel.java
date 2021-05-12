package io.thunder.connection.base;

import io.thunder.connection.extra.ThunderSession;
import io.thunder.packet.Packet;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

public interface ThunderChannel extends Closeable {

    /**
     * Writes a {@link Packet} into the {@link ThunderChannel}
     * (Mostly used for {@link ThunderClient}
     *
     * @param packet the Packet to write
     */
    void processIn(Packet packet);

    /**
     * Sends a {@link Packet} from this {@link ThunderChannel}
     * to (all) other {@link ThunderChannel}s
     *
     * @param packet the Packet to send
     */
    void processOut(Packet packet);

    /**
     * Sends a {@link Packet} from this {@link ThunderChannel}
     * to another {@link ThunderChannel}
     *
     * @param packet the Packet to send
     * @param thunderChannel the Channel to receive it
     */
    void processOut(Packet packet, ThunderChannel thunderChannel);

    /**
     * Flushes this channel
     * and clears useless data
     */
    void flush();

    /**
     * Returns the {@link DataOutputStream}
     * of this {@link ThunderChannel}
     *
     * @return DataOutput
     */
    DataOutputStream getOut();

    /**
     * Returns the {@link DataInputStream}
     * of this {@link ThunderChannel}
     *
     * @return DataInput
     */
    DataInputStream getIn();

    /**
     * This is the Remote Address of the Channel
     *
     * @return SocketAddress
     */
    SocketAddress remoteAddress();

    /**
     * This is the Local Address of the Channel
     *
     * @return SocketAddress
     */
    SocketAddress localAddress();

    /**
     * Returns the {@link ThunderSession}
     * of this {@link ThunderChannel}
     * to identify this channel
     *
     * @return session
     */
    ThunderSession getSession();

    /**
     * Checks if the Channel is opened
     * to receive Data or {@link Packet}s
     *
     * @return boolean if open
     */
    boolean isOpen();

    /**
     * Closes this Channel
     *
     * @throws IOException if something goes wrong
     */
    @Override
    void close() throws IOException;

    /**
     * Checks if the DataConnection is null
     *
     * @return boolean
     */
    boolean isValid();
}
