package io.vera.server.ui;

import io.vera.ui.bossbar.BossBarColor;
import io.vera.ui.bossbar.BossBarDivision;
import io.vera.ui.chat.ChatComponent;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class BossBar {

    private static final Set<UUID> uuids = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final UUID uuid;

    public UUID getUuid() {
        return this.uuid;
    }

    private final AtomicReference<ChatComponent> title = new AtomicReference<>(
            ChatComponent.text("Boss Bar"));

    private final AtomicInteger health = new AtomicInteger();
    private final AtomicReference<BossBarColor> color = new AtomicReference<>(BossBarColor.PINK);
    private final AtomicReference<BossBarDivision> division = new AtomicReference<>(BossBarDivision.NO_DIVISION);
    private final AtomicBoolean darkenSky = new AtomicBoolean();
    private final AtomicBoolean dragonBar = new AtomicBoolean();

    public BossBar(ChatComponent chatComponent, int health, BossBarColor color, BossBarDivision division, boolean darkenSky, boolean dragonBar) {
        this();
        this.title.set(chatComponent);
        this.health.set(health);
        this.color.set(color);
        this.division.set(division);
        this.darkenSky.set(darkenSky);
        this.dragonBar.set(dragonBar);
    }

    public ChatComponent getTitle() {
        return this.title.get();
    }

    public void setTitle(ChatComponent title) {
        ChatComponent old;
        do {
            old = this.title.get();
        } while (title != null && !title.equals(old) &&
                !this.title.compareAndSet(old, title));
    }

    public boolean equals(Object obj) {
        return (obj instanceof BossBar && this.uuid
                .equals(((BossBar)obj).getUuid()));
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }

    public float getHealth() {
        return Float.intBitsToFloat(this.health.get());
    }

    public void setHealth(float health) {
        int old, n = Float.floatToRawIntBits(health);
        do {
            old = this.health.get();
        } while (old != n &&
                !this.health.compareAndSet(old, n));
    }

    public BossBarColor getColor() {
        return this.color.get();
    }

    public void setColor(BossBarColor color) {
        BossBarColor old;
        do {
            old = this.color.get();
        } while (color != null && color != old &&
                !this.color.compareAndSet(old, color));
    }

    public BossBarDivision getDivision() {
        return this.division.get();
    }

    public void setDivision(BossBarDivision division) {
        BossBarDivision old;
        do {
            old = this.division.get();
        } while (division != null && !division.equals(old) &&
                !this.division.compareAndSet(old, division));
    }

    public boolean isDarkenSky() {
        return this.darkenSky.get();
    }

    public void setDarkenSky(boolean darkenSky) {
        boolean old;
        do {
            old = this.darkenSky.get();
        } while (old != darkenSky &&
                !this.darkenSky.compareAndSet(old, darkenSky));
    }

    public boolean isDragonBar() {
        return this.dragonBar.get();
    }

    public void setDragonBar(boolean dragonBar) {
        boolean old;
        do {
            old = this.dragonBar.get();
        } while (old != dragonBar &&
                !this.dragonBar.compareAndSet(old, dragonBar));
    }

    public BossBar clone() {
        return new BossBar(ChatComponent.fromJson(((ChatComponent)this.title.get()).asJson().asVsonObject()), this.health
                .get(), this.color
                .get(), this.division
                .get(), this.darkenSky
                .get(), this.dragonBar
                .get());
    }

    public BossBar() {
        while (true) {
            UUID uuid = UUID.randomUUID();
            if (uuids.add(uuid)) {
                this.uuid = uuid;
                return;
            }
        }
    }
}
