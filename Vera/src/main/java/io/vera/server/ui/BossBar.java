
package io.vera.server.ui;

import lombok.Getter;
import io.vera.ui.bossbar.BossBarColor;
import io.vera.ui.bossbar.BossBarDivision;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@ThreadSafe
public class BossBar {

    private static final Set<UUID> uuids = Collections.newSetFromMap(new ConcurrentHashMap<>());
    @Getter
    private final UUID uuid;

    private final AtomicReference<ChatComponent> title =
            new AtomicReference<>(ChatComponent.text("Boss Bar"));
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

    public BossBar() {
        while (true) {
            UUID uuid = UUID.randomUUID();
            if (uuids.add(uuid)) {
                this.uuid = uuid;
                break;
            }
        }
    }

    public ChatComponent getTitle() {
        return this.title.get();
    }

    public void setTitle(ChatComponent title) {
        ChatComponent old;
        while (true) {
            old = this.title.get();
            if (title != null && !title.equals(old)) {
                if (this.title.compareAndSet(old, title)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof BossBar &&
                this.uuid.equals(((BossBar) obj).getUuid());
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }
    
    public float getHealth() {
        return Float.intBitsToFloat(this.health.get());
    }

    
    public void setHealth(float health) {
        int old;
        int n = Float.floatToRawIntBits(health);

        while (true) {
            old = this.health.get();
            if (old != n) {
                if (this.health.compareAndSet(old, n)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    
    public BossBarColor getColor() {
        return this.color.get();
    }

    
    public void setColor(BossBarColor color) {
        BossBarColor old;
        while (true) {
            old = this.color.get();
            if (color != null && color != old) {
                if (this.color.compareAndSet(old, color)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    
    public BossBarDivision getDivision() {
        return this.division.get();
    }

    
    public void setDivision(BossBarDivision division) {
        BossBarDivision old;
        while (true) {
            old = this.division.get();
            if (division != null && !division.equals(old)) {
                if (this.division.compareAndSet(old, division)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    
    public boolean isDarkenSky() {
        return this.darkenSky.get();
    }

    
    public void setDarkenSky(boolean darkenSky) {
        boolean old;

        while (true) {
            old = this.darkenSky.get();
            if (old != darkenSky) {
                if (this.darkenSky.compareAndSet(old, darkenSky)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    
    public boolean isDragonBar() {
        return this.dragonBar.get();
    }

    
    public void setDragonBar(boolean dragonBar) {
        boolean old;

        while (true) {
            old = this.dragonBar.get();
            if (old != dragonBar) {
                if (this.dragonBar.compareAndSet(old, dragonBar)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    
    public BossBar clone() {
        return new BossBar(ChatComponent.fromJson(this.title.get().asJson().asVsonObject()),
                this.health.get(),
                this.color.get(),
                this.division.get(),
                this.darkenSky.get(),
                this.dragonBar.get());
    }
}