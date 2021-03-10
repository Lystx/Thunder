
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@ThreadSafe
public interface Player extends EntityLiving, CommandSource {

    float DEFAULT_FLYING_SPEED = 0.159F;

    float DEFAULT_WALKING_SPEED = 0.699999988079071F;

    float DEFAULT_SPRINT_SPEED = 0.30000001192092896F;

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

    void setTabListName(ChatComponent name);

    void sendMessage(ChatComponent message, ChatType type);

    @Override
    default void sendMessage(ChatComponent message) {
        this.sendMessage(message, ChatType.SYSTEM);
    }

    default void sendMessage(String message) {
        this.sendMessage(ChatComponent.text(message));
    }

    void kick(ChatComponent reason);

    void chat(String msg);

    GameMode getGameMode();

    void setGameMode(GameMode gameMode);

    TabList getTabList();

    void setTabList(TabList tabList);

    List<BossBar> getBossBars();

    void addBossBar(BossBar bar);

    void removeBossBar(BossBar bar);

    void updateBossBars();

    void sendTitle(Title title);

    void resetTitle();

    PlayerInventory getInventory();

    void openInventory(Inventory inventory);

    boolean isGodMode();


    void setGodMode(boolean godMode);

    boolean canFly();

    void setCanFly(boolean canFly);

    boolean isFlying();

    void setFlying(boolean flying);

    float getFlyingSpeed();

    void setFlyingSpeed(float flyingSpeed);

    float getWalkingSpeed();

    void setWalkingSpeed(float walkingSpeed);
}