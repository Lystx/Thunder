package io.thunder.connection.base;


import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.data.ThunderConnection;

import java.util.List;
import java.util.UUID;

/**
 * This class is mostly used to identify
 * {@link ThunderClient}s or {@link ThunderServer}s
 *
 * This {@link ThunderSession} contains a Unique ID
 * and a name for every Session that is created
 *
 */
public interface ThunderSession {

    /**
     * This is the ID of the Session
     *
     * @return name of Session as {@link String}
     */
    String getSessionId();

    /**
     * Sets the SessionID of this Session
     *
     * @param sessionId the ID as {@link String}
     */
    void setSessionId(String sessionId);

    /**
     * Sets the uniqueId of this Session
     *
     * @param uniqueId the ID as {@link UUID}
     */
    void setUniqueId(UUID uniqueId);

    /**
     * Sets the startTime of this Session
     *
     * @param startTime the time as {@link Long}
     */
    void setStartTime(long startTime);

    /**
     * This is the {@link UUID} of the Session
     * to identify it later (Developer purposes)
     *
     * @return Unique Id of Session as {@link UUID}
     */
    UUID getUniqueId();

    /**
     * Those are all the {@link ThunderSession}s that are
     * connected to this {@link ThunderSession}
     * Makes most sense in {@link ThunderServer} when
     * all the {@link ThunderClient}s are connected
     *
     * @return List with {@link ThunderSession}s
     */
    List<ThunderSession> getConnectedSessions();

    /**
     * This is the Channel where all the Data
     * is send through
     *
     * @return Channel of Session
     */
    ThunderChannel getChannel();

    /**
     * Sets the {@link ThunderChannel} of this {@link ThunderSession}
     *
     * @param channel the Channel of this Session
     */
    void setChannel(ThunderChannel channel);

    /**
     * Checks if the {@link ThunderSession} is connected
     *
     * @return boolean if connected
     */
    boolean isHandShaked();

    /**
     * Sets the Authentication of this {@link ThunderSession}
     *
     * @param b if it's authenticated or not
     */
    void setHandShaked(boolean b);

    /**
     * Returns the {@link ThunderConnection}
     * of this given {@link ThunderConnection}
     *
     * @return connection of session
     */
    ThunderConnection getConnection();

    /**
     * Disconnects the Session
     * using the {@link ThunderSession#getConnection()}
     */
    default void disconnect() {
        this.getConnection().disconnect();
    }

    /**
     * Checks if Session is connected
     * using the {@link ThunderSession#getConnection()}
     */
    default boolean isConnected() {
        return this.getConnection().isConnected();
    }

    /**
     * this is when the Session was started!
     *
     * @return Start time of session as long
     */
    long getStartTime();

    /**
     * Returns a Session of the {@link ThunderSession#getConnectedSessions()}
     *
     * @param uuid the {@link UUID} of the session you search
     * @return Session by UUID
     */
    ThunderSession getSession(UUID uuid);
}
