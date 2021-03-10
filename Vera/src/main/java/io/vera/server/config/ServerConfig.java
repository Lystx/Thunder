
package io.vera.server.config;

import lombok.Getter;
import io.vera.util.Misc;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.Path;

@ThreadSafe
public class ServerConfig extends Config {
    public static final Path PATH = Misc.HOME_PATH.resolve("server.vson");

    private volatile String ip;
    private volatile int port;
    private volatile boolean useNative;
    private volatile int compressionThresh;
    private volatile boolean useAuth;
    private volatile int maxPlayers;
    private volatile String motd;
    @Getter
    private volatile boolean nettyLeakDetectorEnabled;

    public ServerConfig() {
        super(PATH);
    }

    public static ServerConfig init() throws IOException {
        ServerConfig config = new ServerConfig();
        config.load();
        return config;
    }

    public String ip() {
        return this.ip;
    }

    public int port() {
        return this.port;
    }

    public boolean useNative() {
        return this.useNative;
    }

    public int compressionThresh() {
        return this.compressionThresh;
    }

    public boolean doAuth() {
        return this.useAuth;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    public String motd() {
        return this.motd;
    }

    @Override
    public void load() throws IOException {
        super.load();

        this.ip = this.getString("address");
        this.port = this.getInt("port");
        this.useNative = this.getBoolean("use-native");
        this.compressionThresh = this.getInt("compression-threshold");
        this.useAuth = this.getBoolean("online-mode");
        this.maxPlayers = this.getInt("max-players");
        this.motd = this.getString("motd");
        this.nettyLeakDetectorEnabled = this.getBoolean("netty-leak-detector");
    }
}