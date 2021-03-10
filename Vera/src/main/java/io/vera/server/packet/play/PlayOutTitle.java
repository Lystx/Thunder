
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.ui.Title;
import io.vera.ui.chat.ChatComponent;

/**
 * Packet used to display and handle text to be displayed
 * in the center of the player's screen.
 */
public abstract class PlayOutTitle extends PacketOut {
    /**
     * Cached instance of an empty title
     */
    private static final ChatComponent EMPTY_TITLE = ChatComponent.empty();

    private final PlayOutTitleType action;

    private PlayOutTitle(PlayOutTitleType action) {
        super(PlayOutTitle.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, this.action.ordinal());
    }

    public static class SetTitle extends PlayOutTitle {
        private final ChatComponent chat;

        public SetTitle(Title title) {
            this(title.getHeader());
        }

        public SetTitle(ChatComponent chat) {
            super(PlayOutTitleType.SET_TITLE);
            this.chat = chat == null ? PlayOutTitle.EMPTY_TITLE : chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            NetData.wstr(buf, this.chat.toString());
        }
    }

    public static class SetSubtitle extends PlayOutTitle {
        private final ChatComponent chat;

        public SetSubtitle(Title title) {
            this(title.getSubtitle());
        }

        public SetSubtitle(ChatComponent chat) {
            super(PlayOutTitleType.SET_SUBTITLE);
            this.chat = chat == null ? PlayOutTitle.EMPTY_TITLE : chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            NetData.wstr(buf, this.chat.toString());
        }
    }

    public static class SetActionBar extends PlayOutTitle {
        private final ChatComponent chat;

        public SetActionBar(ChatComponent chat) {
            super(PlayOutTitleType.SET_ACTION_BAR);
            this.chat = chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            NetData.wstr(buf, this.chat.toString());
        }
    }

    public static class SetTiming extends PlayOutTitle {
        private final int fadeIn;
        private final int stay;
        private final int fadeOut;

        public SetTiming() {
            this(Title.DEFAULT_FADE_IN, Title.DEFAULT_STAY, Title.DEFAULT_FADE_OUT);
        }

        public SetTiming(Title title) {
            this(title.getFadeIn(), title.getStay(), title.getFadeOut());
        }

        public SetTiming(int fadeIn, int stay, int fadeOut) {
            super(PlayOutTitleType.SET_TIMES_AND_DISPLAY);
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            buf.writeInt(this.fadeIn);
            buf.writeInt(this.stay);
            buf.writeInt(this.fadeOut);
        }
    }

    public static class Hide extends PlayOutTitle {
        public Hide() {
            super(PlayOutTitleType.HIDE);
        }
    }

    public static class Reset extends PlayOutTitle {
        public Reset() {
            super(PlayOutTitleType.RESET);
        }
    }

    public enum PlayOutTitleType {
        SET_TITLE, SET_SUBTITLE, SET_ACTION_BAR, SET_TIMES_AND_DISPLAY, HIDE, RESET
    }
}
