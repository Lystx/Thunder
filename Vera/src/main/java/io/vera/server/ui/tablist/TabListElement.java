
package io.vera.server.ui.tablist;

import io.vera.world.opt.GameMode;
import lombok.Data;
import io.vera.server.player.VeraPlayer;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@ThreadSafe
public class TabListElement {
    private final UUID uuid;
    private volatile GameMode gameMode;
    private volatile String name;
    private volatile int ping;
    private volatile ChatComponent displayName;
    private volatile boolean blank;

    private final List<PlayerProperty> properties;

    public TabListElement() {
        this.uuid = UUID.randomUUID();
        this.properties = Collections.emptyList();
    }

    public TabListElement(VeraPlayer player) {
        this.uuid = player.getUuid();
        this.name = player.getName();
        this.displayName = player.getTabListName();
        this.ping = player.net().getPing().intValue();
        this.gameMode = player.getGameMode();

        PlayerProperty textures = player.getSkinTextures();
        if (textures != null) {
            this.properties = Collections.singletonList(textures);
        } else {
            this.properties = Collections.emptyList();
        }
    }

}
