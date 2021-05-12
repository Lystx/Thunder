package io.thunder.impl.other;

import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderChannel;
import io.thunder.connection.extra.ThunderSession;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProvidedThunderSession implements ThunderSession {

    private String sessionId; //The name
    private final UUID uniqueId; //the uuid
    private final List<ThunderSession> connectedSessions; //The sessions
    private final long startTime; //The time it started
    private final ThunderConnection connection; //The connection

    @Setter
    private ThunderChannel channel; //The channel of this session

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

    @Setter
    private boolean authenticated;

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
