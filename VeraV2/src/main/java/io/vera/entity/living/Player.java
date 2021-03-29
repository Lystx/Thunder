package io.vera.entity.living;

import io.vera.Impl;
import io.vera.command.CommandSource;
import io.vera.inventory.Inventory;
import io.vera.inventory.PlayerInventory;
import io.vera.server.ui.BossBar;
import io.vera.server.ui.Title;
import io.vera.server.ui.tablist.TabList;
import io.vera.ui.chat.ChatComponent;
import io.vera.ui.chat.ChatType;
import io.vera.world.opt.GameMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Player extends EntityLiving, CommandSource {

    float DEFAULT_FLYING_SPEED = 0.159F;
    float DEFAULT_WALKING_SPEED = 0.7F;
    float DEFAULT_SPRINT_SPEED = 0.3F;

    @Nullable
    static Player byName(String name) {
        return Impl.get().getByName(name);
    }

    @Nullable
    static Player byUuid(UUID uuid) {
        return Impl.get().getByUuid(uuid);
    }

    @Nonnull
    static Map<String, Player> search(String search) {
        return Impl.get().findByName(search);
    }

    static Map<String, Player> fuzzySearch(String filter) {
        return Impl.get().findByNameFuzzy(filter);
    }

    String getName();

    UUID getUuid();

    ChatComponent getTabListName();

    void setTabListName(ChatComponent paramChatComponent);

    void sendMessage(ChatComponent paramChatComponent, ChatType paramChatType);

    default void sendMessage(ChatComponent message) {
        sendMessage(message, ChatType.SYSTEM);
    }

    default void sendMessage(String message) {
        sendMessage(ChatComponent.text(message));
    }

    void kick(ChatComponent paramChatComponent);

    void chat(String paramString);

    GameMode getGameMode();

    void setGameMode(GameMode paramGameMode);

    TabList getTabList();

    void setTabList(TabList paramTabList);

    List<BossBar> getBossBars();

    void addBossBar(BossBar paramBossBar);

    void removeBossBar(BossBar paramBossBar);

    void updateBossBars();

    void sendTitle(Title paramTitle);

    void resetTitle();

    PlayerInventory getInventory();

    void openInventory(Inventory paramInventory);

    boolean isGodMode();

    void setGodMode(boolean paramBoolean);

    boolean canFly();

    void setCanFly(boolean paramBoolean);

    boolean isFlying();

    void setFlying(boolean paramBoolean);

    float getFlyingSpeed();

    void setFlyingSpeed(float paramFloat);

    float getWalkingSpeed();

    void setWalkingSpeed(float paramFloat);
}
