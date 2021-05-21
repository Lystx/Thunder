package io.thunder.impl.other;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.connection.base.ThunderSession;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProvidedThunderSession implements ThunderSession {

    /**
     * The name of this session
     */
    private String sessionId;

    /**
     * The UUID of the session
     */
    private UUID uniqueId;

    /**
     * The sessions
     */
    private List<ThunderSession> connectedSessions;

    /**
     * The time it started
     */
    private long startTime;

    /**
     * The connection
     */
    private ThunderConnection connection;

    /**
     * The channel of this session
     */
    @Setter
    private ThunderChannel channel;

    /**
     * If the session has handshaked the server
     */
    @Setter
    private boolean handShaked;

    /**
     * Creates a new instance of the {@link ThunderSession}
     *
     * @param sessionId the name
     * @param uniqueId the uuid
     * @param connectedSessions the connected sessions
     * @param startTime the startTime
     * @param connection the connection
     * @param channel the channel
     * @param authenticated if its authenticated (default false)
     *
     * @return new Session
     */
    public static ThunderSession newInstance(String sessionId, UUID uniqueId, List<ThunderSession> connectedSessions, long startTime, ThunderConnection connection, ThunderChannel channel, boolean authenticated) {
        return new ProvidedThunderSession(sessionId, uniqueId, connectedSessions, startTime, connection, channel, authenticated);
    }


    /**
     * Sets the name of the Session
     * @param sessionId the ID as {@link String}
     */
    public void setSessionId(String sessionId) {
        this.sessionId = "ThunderSession#" + sessionId;
    }

    /**
     * Returns the name of the Session
     *
     * @return sessionId
     */
    public String getSessionId() {
        return "ThunderSession#" + sessionId;
    }

    /**
     * Returns a {@link ProvidedThunderSession} by its {@link UUID}
     *
     * @param uuid the UUID of the Session
     * @return the searched Session
     */
    public ThunderSession getSession(UUID uuid) {
        return this.connectedSessions.stream().filter(implThunderSession -> implThunderSession.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }
}
