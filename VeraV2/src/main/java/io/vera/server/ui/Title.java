package io.vera.server.ui;

import io.vera.ui.chat.ChatComponent;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Title {
    private ChatComponent header;

    private ChatComponent subtitle;

    public ChatComponent getHeader() {
        return this.header;
    }

    public ChatComponent getSubtitle() {
        return this.subtitle;
    }

    public static int DEFAULT_FADE_IN = 10;

    public static int DEFAULT_STAY = 70;

    public static int DEFAULT_FADE_OUT = 20;

    private int fadeIn = DEFAULT_FADE_IN;

    public int getFadeIn() {
        return this.fadeIn;
    }

    private int stay = DEFAULT_STAY;

    public int getStay() {
        return this.stay;
    }

    private int fadeOut = DEFAULT_FADE_OUT;

    public int getFadeOut() {
        return this.fadeOut;
    }

    public Title setHeader(ChatComponent title) {
        this.header = title;
        return this;
    }

    public Title setSubtitle(ChatComponent subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public boolean isDefaultFadeTimes() {
        return (this.fadeIn == DEFAULT_FADE_IN && this.stay == DEFAULT_STAY && this.fadeOut == DEFAULT_FADE_OUT);
    }
}
