
package io.vera.event.server;

import java.net.InetSocketAddress;
import java.util.UUID;

import io.vera.event.base.Event;
import io.vera.ui.chat.ChatComponent;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
public class ServerPingEvent extends Event {

    private final InetSocketAddress pinger;
    private final ServerPingResponse response;

    @Data
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ServerPingResponse {

        @NonNull
        private ServerPingResponseVersion version;

        @NonNull
        private ServerPingResponsePlayers players;

        @NonNull
        private ChatComponent description;
        private String serverIconBase64;

        public VsonObject asJson() {
            VsonObject json = new VsonObject();
            json.append("version", version.asJson());
            json.append("players", players.asJson());
            json.append("description", description.asJson());
            if (serverIconBase64 != null) {
                json.append("favicon", serverIconBase64);
            }
            return json;
        }

    }

    @Data
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ServerPingResponseVersion {

        @NonNull
        private String name;
        private int protocol;

        public VsonObject asJson() {
            VsonObject json = new VsonObject();
            json.append("name", name);
            json.append("protocol", protocol);
            return json;
        }

    }

    @Data
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ServerPingResponsePlayers {

        private int onlinePlayers;
        private int maxPlayers;

        @NonNull
        private ServerPingResponseSample[] samples;

        public ServerPingResponsePlayers setOnlinePlayers(int onlinePlayers) {
            if (onlinePlayers < 0)
                throw new IllegalArgumentException("invalid number of players");
            this.onlinePlayers = onlinePlayers;
            return this;
        }

        public ServerPingResponsePlayers setMaxPlayers(int maxPlayers) {
            if (maxPlayers < 0)
                throw new IllegalArgumentException("invalid number of players");
            this.maxPlayers = maxPlayers;
            return this;
        }

        public VsonObject asJson() {
            VsonObject json = new VsonObject();
            json.append("max", maxPlayers);
            json.append("online", onlinePlayers);
            VsonArray sampleArray = new VsonArray();
            for (ServerPingResponseSample sample : samples) {
                if (sample != null) {
                    sampleArray.append(sample.asJson());
                }
            }
            json.append("sample", sampleArray);
            return json;
        }

    }

    @Data
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ServerPingResponseSample {

        @NonNull
        private String name;

        @NonNull
        private UUID uuid;

        public VsonObject asJson() {
            VsonObject json = new VsonObject();
            json.append("name", name);
            json.append("id", uuid.toString());
            return json;
        }

    }

}
