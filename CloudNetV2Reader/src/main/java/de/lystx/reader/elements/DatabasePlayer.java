package de.lystx.reader.elements;


import java.util.UUID;

public class DatabasePlayer {

    private final String name;
    private final UUID uniqueId;
    private final String ipAddress;

    private final long lastLogin;
    private final long firstLogin;

    public DatabasePlayer(String name, UUID uniqueId, String ipAddress, long lastLogin, long firstLogin) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.lastLogin = lastLogin;
        this.firstLogin = firstLogin;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public long getFirstLogin() {
        return firstLogin;
    }
}
