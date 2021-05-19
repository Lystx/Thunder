package io.thunder.connection;

import io.thunder.packet.Packet;

/**
 * Custom ErrorHandler
 */
public interface ErrorHandler {

    /**
     * Called when an Exception is thrown anywhere
     *
     * @param e the exception
     */
    void onError(Exception e);

    /**
     * Called when a Packet couldn't be handled
     *
     * @param packet the packet (might be null)
     * @param _class the class as String (because Class.class does not exist in failure)
     * @param e the exception thrown (might be null)
     */
    void onPacketFailure(Packet packet, String _class, Exception e);
}
