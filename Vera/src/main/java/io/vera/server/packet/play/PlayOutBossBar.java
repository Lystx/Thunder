
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.ui.BossBar;
import io.vera.ui.bossbar.BossBarColor;
import io.vera.ui.bossbar.BossBarDivision;
import io.vera.ui.chat.ChatComponent;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;


public abstract class PlayOutBossBar extends PacketOut {

    protected final BossBar bossBar;
    private final int action;

    public PlayOutBossBar(BossBar bossBar, int action) {
        super(PlayOutBossBar.class);
        this.bossBar = bossBar;
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.bossBar.getUuid().getMostSignificantBits());
        buf.writeLong(this.bossBar.getUuid().getLeastSignificantBits());
        NetData.wvint(buf, this.action);
    }

    @Immutable
    public static class Add extends PlayOutBossBar {
        private final String title;
        private final float health;
        private final int colorId;
        private final int divisionId;
        private final boolean darkenSky;
        private final boolean dragonBar;

        public Add(BossBar bossBar) {
            super(bossBar, 0);
            this.title = bossBar.getTitle().toString();
            this.health = bossBar.getHealth();
            this.colorId = bossBar.getColor().getId();
            this.divisionId = bossBar.getDivision().getId();
            this.darkenSky = bossBar.isDarkenSky();
            this.dragonBar = bossBar.isDragonBar();
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wstr(buf, this.title);
            buf.writeFloat(this.health);
            NetData.wvint(buf, this.colorId);
            NetData.wvint(buf, this.divisionId);

            int flags = 0;
            if (this.darkenSky)
                flags |= 0x1;
            if (this.dragonBar)
                flags |= 0x2;
            buf.writeByte(flags);
        }

    }

    @Immutable
    public static class Remove extends PlayOutBossBar {
        public Remove(BossBar bossBar) {
            super(bossBar, 1);
        }
    }

    @Getter
    @Immutable
    public static class UpdateHealth extends PlayOutBossBar {
        private final float health;

        public UpdateHealth(BossBar bossBar) {
            super(bossBar, 2);
            this.health = bossBar.getHealth();
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            buf.writeFloat(this.health);
        }
    }

    @Getter
    @Immutable
    public static class UpdateTitle extends PlayOutBossBar {
        private final ChatComponent title;

        public UpdateTitle(BossBar bossBar) {
            super(bossBar, 3);
            this.title = bossBar.getTitle();
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wstr(buf, this.title.toString());
        }
    }

    @Getter
    @Immutable
    public static class UpdateStyle extends PlayOutBossBar {
        private final BossBarColor color;
        private final BossBarDivision division;

        public UpdateStyle(BossBar bossBar) {
            super(bossBar, 4);
            this.color = bossBar.getColor();
            this.division = bossBar.getDivision();
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wvint(buf, color.getId());
            NetData.wvint(buf, division.getId());
        }
    }

    @Getter
    @Immutable
    public static class UpdateFlags extends PlayOutBossBar {
        private final boolean darkenSky;
        private final boolean dragonBar;

        public UpdateFlags(BossBar bossBar) {
            super(bossBar, 5);
            this.darkenSky = bossBar.isDarkenSky();
            this.dragonBar = bossBar.isDragonBar();
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            int flags = 0;
            if (this.darkenSky)
                flags |= 0x1;
            if (this.dragonBar)
                flags |= 0x2;
            buf.writeByte(flags);
        }
    }
}