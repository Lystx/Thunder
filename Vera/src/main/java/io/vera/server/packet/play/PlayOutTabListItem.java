
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vera.server.ui.tablist.TabListElement;
import io.vera.ui.chat.ChatComponent;
import io.vera.world.opt.GameMode;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;


@Immutable
public abstract class PlayOutTabListItem extends PacketOut {

    private final ActionType action;

    private PlayOutTabListItem(ActionType action) {
        super(PlayOutTabListItem.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.action.ordinal());
        NetData.wvint(buf, this.getActionCount());
    }

    public abstract int getActionCount();

    public static AddPlayer addPlayerPacket() {
        return new AddPlayer();
    }

    public static RemovePlayer removePlayerPacket() {
        return new RemovePlayer();
    }

    public static UpdateDisplayName updatePlayerPacket() {
        return new UpdateDisplayName();
    }

    public static UpdateGameMode updateGamemodePacket() {
        return new UpdateGameMode();
    }

    public static UpdateLatency updateLatencyPacket() {
        return new UpdateLatency();
    }

    @NotThreadSafe
    public static class AddPlayer extends PlayOutTabListItem {
        private final Collection<PlayerData> additions = new HashSet<>();

        public AddPlayer() {
            super(ActionType.ADD_PLAYER);
        }

        public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName) {
            this.addPlayer(uuid, name, gameMode, ping, displayName, null);
        }

        public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName, List<PlayerProperty> properties) {
            PlayerData playerData = new PlayerData(uuid, name, gameMode, ping, displayName, properties);
            this.additions.add(playerData);
        }

        public void addPlayer(TabListElement element) {
            PlayerData playerData = new PlayerData(element.getUuid(),
                    element.getName(),
                    element.getGameMode(),
                    element.getPing(),
                    element.getDisplayName(),
                    element.getProperties());
            this.additions.add(playerData);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.additions.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                NetData.wstr(buf, data.name);
                NetData.wvint(buf, data.properties != null ? data.properties.size() : 0);
                if (data.properties != null) {
                    data.properties.forEach(playerProperty -> {
                        NetData.wstr(buf, playerProperty.getName());
                        NetData.wstr(buf, playerProperty.getValue());
                        buf.writeBoolean(playerProperty.getSignature() != null);
                        if (playerProperty.getSignature() != null) {
                            NetData.wstr(buf, playerProperty.getSignature());
                        }
                    });
                }
                NetData.wvint(buf, data.gameMode.asInt());
                NetData.wvint(buf, data.ping);
                buf.writeBoolean(data.displayName != null);
                if (data.displayName != null) {
                    NetData.wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        public int getActionCount() {
            return this.additions.size();
        }

        @Immutable
        @EqualsAndHashCode(of = "uuid")
        @RequiredArgsConstructor
        private class PlayerData {
            private final UUID uuid;
            private final String name;
            private final GameMode gameMode;
            private final int ping;
            private final ChatComponent displayName;
            private final List<PlayerProperty> properties;
        }
    }

    @NotThreadSafe
    public static class RemovePlayer extends PlayOutTabListItem {
        private final Collection<UUID> removals = new HashSet<>();

        public RemovePlayer() {
            super(ActionType.REMOVE_PLAYER);
        }

        public void removePlayer(UUID uuid) {
            this.removals.add(uuid);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.removals.forEach(uuid -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
            });
        }

        @Override
        public int getActionCount() {
            return this.removals.size();
        }
    }

    @NotThreadSafe
    public static class UpdateGameMode extends PlayOutTabListItem {
        private final Map<UUID, GameMode> updates = new HashMap<>();

        public UpdateGameMode() {
            super(ActionType.UPDATE_GAMEMODE);
        }

        public void update(UUID uuid, GameMode gameMode) {
            this.updates.put(uuid, gameMode);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach((uuid, gameMode) -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
                NetData.wvint(buf, gameMode.asInt());
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }
    }

    @NotThreadSafe
    public static class UpdateLatency extends PlayOutTabListItem {
        private final Map<UUID, Integer> updates = new HashMap<>();

        public UpdateLatency() {
            super(ActionType.UPDATE_LATENCY);
        }

        public void update(UUID uuid, int latency) {
            this.updates.put(uuid, latency);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach((uuid, latency) -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
                NetData.wvint(buf, latency);
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }
    }

    @NotThreadSafe
    public static class UpdateDisplayName extends PlayOutTabListItem {
        private final Collection<PlayerData> updates = new HashSet<>();

        public UpdateDisplayName() {
            super(ActionType.UPDATE_DISPLAY_NAME);
        }

        public void update(UUID uuid, ChatComponent displayName) {
            PlayerData data = new PlayerData(uuid, displayName);
            this.updates.add(data);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                buf.writeBoolean(data.displayName != null);
                if (data.displayName != null) {
                    NetData.wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }

        @Immutable
        @EqualsAndHashCode(of = "uuid")
        @RequiredArgsConstructor
        private final class PlayerData {
            private final UUID uuid;
            private final ChatComponent displayName;
        }
    }

    @Immutable
    public enum ActionType {
        ADD_PLAYER, UPDATE_GAMEMODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER
    }
}