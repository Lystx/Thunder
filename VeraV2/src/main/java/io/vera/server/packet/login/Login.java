package io.vera.server.packet.login;

import io.vera.server.VeraServer;
import io.vera.server.net.NetClient;
import io.vera.server.player.VeraPlayer;
import io.vera.server.util.Cache;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Login {
    private static final AtomicInteger LOGGING_IN = new AtomicInteger();
    private static final Cache<String, UUID> UUID_CACHE = new Cache(300000L);
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static boolean canLogin(NetClient client) {
        if (LOGGING_IN.get() + VeraPlayer.getPlayers().size() >=
                VeraServer.cfg().maxPlayers()) {
            client.disconnect("Server is full");
            return false;
        }
        String name = client.getName();
        if (VeraPlayer.getPlayerNames().containsKey(name)) {
            client.disconnect("Player with name \"" + name + "\" already exists");
            return false;
        }
        LOGGING_IN.incrementAndGet();
        return true;
    }

    public static void finish() {
        LOGGING_IN.decrementAndGet();
    }

    public static UUID convert(String name, final String input) {
        return UUID_CACHE.get(name, new Supplier<UUID>() {
            public UUID get() {
                return UUID.fromString(Login.UUID_PATTERN.matcher(input).replaceAll("$1-$2-$3-$4-$5"));
            }
        });
    }
}
