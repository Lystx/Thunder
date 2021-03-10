
package io.vera.ui.chat;

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum ChatColor {

    BLACK('0'),

    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    @Getter
    public static final char escape = '\u00A7';

    @Getter
    private final char colorChar;

    ChatColor(char colorChar) {
        this.colorChar = colorChar;
    }

    public boolean isFormat() {
        return 'k' <= this.colorChar && this.colorChar <= 'r';
    }

    public boolean isColor() {
        return !this.isFormat();
    }

    @Override
    public String toString() {
        return String.valueOf(escape) + this.colorChar;
    }

    @Nonnull
    public static ChatColor of(char colorChar) {
        for (ChatColor color : values()) {
            if (color.colorChar == colorChar) {
                return color;
            }
        }

        throw new IllegalArgumentException("no color with character " + colorChar);
    }
}
