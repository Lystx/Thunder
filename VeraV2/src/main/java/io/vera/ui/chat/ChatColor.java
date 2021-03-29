package io.vera.ui.chat;

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

    public static final char escape = '§';

    private final char colorChar;

    public static char getEscape() {
        return '§';
    }

    public char getColorChar() {
        return this.colorChar;
    }

    ChatColor(char colorChar) {
        this.colorChar = colorChar;
    }

    public boolean isFormat() {
        return ('k' <= this.colorChar && this.colorChar <= 'r');
    }

    public boolean isColor() {
        return !isFormat();
    }

    public String toString() {
        return String.valueOf('§') + this.colorChar;
    }

    @Nonnull
    public static ChatColor of(char colorChar) {
        for (ChatColor color : values()) {
            if (color.colorChar == colorChar)
                return color;
        }
        throw new IllegalArgumentException("no color with character " + colorChar);
    }
}
